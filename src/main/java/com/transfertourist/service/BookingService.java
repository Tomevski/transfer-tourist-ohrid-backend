package com.transfertourist.service;

import com.transfertourist.constants.BookingStatus;
import com.transfertourist.constants.TripType;
import com.transfertourist.dto.request.BookingCreateRequest;
import com.transfertourist.dto.response.BookingResponse;
import com.transfertourist.entity.Booking;
import com.transfertourist.entity.TransferPrice;
import com.transfertourist.entity.Vehicle;
import com.transfertourist.exception.BusinessRuleException;
import com.transfertourist.exception.ResourceNotFoundException;
import com.transfertourist.mapper.BookingMapper;
import com.transfertourist.repository.AppSettingRepository;
import com.transfertourist.repository.BookingRepository;
import com.transfertourist.repository.TransferPriceRepository;
import com.transfertourist.repository.VehicleRepository;
import com.transfertourist.util.ReferenceCodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Booking creation with server-authoritative pricing and business rules (CR-2).
 * The client-sent total is ignored: the fare is resolved from the transfer-price
 * records for the chosen route(s) + vehicle, and infant seats are charged per
 * leg using the {@code infant_seat_price} app setting.
 */
@Service
public class BookingService {

    private static final String INFANT_SEAT_PRICE_KEY = "infant_seat_price";
    private static final BigDecimal DEFAULT_INFANT_SEAT_PRICE = BigDecimal.TEN;

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final TransferPriceRepository transferPriceRepository;
    private final AppSettingRepository appSettingRepository;
    private final BookingMapper bookingMapper;
    private final ReferenceCodeGenerator referenceCodeGenerator;

    public BookingService(BookingRepository bookingRepository,
                          VehicleRepository vehicleRepository,
                          TransferPriceRepository transferPriceRepository,
                          AppSettingRepository appSettingRepository,
                          BookingMapper bookingMapper,
                          ReferenceCodeGenerator referenceCodeGenerator) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.transferPriceRepository = transferPriceRepository;
        this.appSettingRepository = appSettingRepository;
        this.bookingMapper = bookingMapper;
        this.referenceCodeGenerator = referenceCodeGenerator;
    }

    @Transactional
    public BookingResponse create(BookingCreateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", request.vehicleId()));

        // Capacity is checked against passengers only; infant seats are an add-on.
        if (request.passengers() > vehicle.getCapacity()) {
            throw new BusinessRuleException(
                    "The selected vehicle cannot carry " + request.passengers() + " passengers.");
        }

        boolean isReturn = request.tripType() == TripType.RETURN && request.returnLeg() != null;

        BigDecimal outboundPrice = resolvePrice(
                request.outbound().fromLocationId(), request.outbound().toLocationId(), vehicle.getId())
                .orElseThrow(() -> new BusinessRuleException(
                        "No price is configured for the selected route and vehicle."));

        // Return leg falls back to the outbound price when the reverse route is unpriced.
        BigDecimal returnPrice = isReturn
                ? resolvePrice(request.returnLeg().fromLocationId(), request.returnLeg().toLocationId(), vehicle.getId())
                    .orElse(outboundPrice)
                : null;

        int legs = isReturn ? 2 : 1;
        BigDecimal infantSeatsTotal = infantSeatPrice()
                .multiply(BigDecimal.valueOf((long) request.infantSeats() * legs));
        BigDecimal total = outboundPrice
                .add(returnPrice != null ? returnPrice : BigDecimal.ZERO)
                .add(infantSeatsTotal);

        Booking booking = new Booking();
        booking.setId("bkg-" + UUID.randomUUID());
        booking.setReferenceCode(uniqueReferenceCode());
        booking.setStatus(BookingStatus.PENDING);
        booking.setTripType(request.tripType());
        booking.setPassengers(request.passengers());
        booking.setInfantSeats(request.infantSeats());
        booking.setComments(request.comments());
        booking.setVehicle(vehicle);
        booking.setTotalPrice(total);
        booking.setCustomer(bookingMapper.toCustomerEntity(request.customer()));
        booking.setOutbound(bookingMapper.toLegEntity(request.outbound()));
        booking.setReturnLeg(isReturn ? bookingMapper.toLegEntity(request.returnLeg()) : null);
        booking.setCreatedAt(Instant.now());

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    private Optional<BigDecimal> resolvePrice(String fromId, String toId, String vehicleId) {
        return transferPriceRepository
                .findByFromLocation_IdAndToLocation_IdAndVehicle_Id(fromId, toId, vehicleId)
                .map(TransferPrice::getPrice);
    }

    private BigDecimal infantSeatPrice() {
        return appSettingRepository.findById(INFANT_SEAT_PRICE_KEY)
                .map(setting -> new BigDecimal(setting.getValue()))
                .orElse(DEFAULT_INFANT_SEAT_PRICE);
    }

    private String uniqueReferenceCode() {
        String code;
        do {
            code = referenceCodeGenerator.generate();
        } while (bookingRepository.existsByReferenceCode(code));
        return code;
    }
}
