package com.transfertourist.service;

import com.transfertourist.constants.BookingStatus;
import com.transfertourist.constants.TripType;
import com.transfertourist.dto.common.PageResponse;
import com.transfertourist.dto.request.BookingCreateRequest;
import com.transfertourist.dto.response.BookingResponse;
import com.transfertourist.entity.Booking;
import com.transfertourist.entity.TransferPrice;
import com.transfertourist.entity.Vehicle;
import com.transfertourist.exception.BusinessRuleException;
import com.transfertourist.exception.ResourceNotFoundException;
import com.transfertourist.mapper.BookingMapper;
import com.transfertourist.repository.BookingRepository;
import com.transfertourist.repository.TransferPriceRepository;
import com.transfertourist.repository.VehicleRepository;
import com.transfertourist.util.ReferenceCodeGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
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

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final TransferPriceRepository transferPriceRepository;
    private final SettingService settingService;
    private final BookingMapper bookingMapper;
    private final ReferenceCodeGenerator referenceCodeGenerator;

    public BookingService(BookingRepository bookingRepository,
                          VehicleRepository vehicleRepository,
                          TransferPriceRepository transferPriceRepository,
                          SettingService settingService,
                          BookingMapper bookingMapper,
                          ReferenceCodeGenerator referenceCodeGenerator) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.transferPriceRepository = transferPriceRepository;
        this.settingService = settingService;
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
        BigDecimal infantSeatsTotal = settingService.infantSeatPrice()
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

    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * Admin: paged, filterable list of bookings (newest first). Mirrors the mock:
     * 1-based page, default page size 10, {@code status} filter (blank or
     * {@code "ALL"} = all), free-text {@code q} over reference/customer/route.
     */
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> adminList(String status, String q, Integer page, Integer pageSize) {
        int pageNumber = page == null ? 1 : Math.max(1, page);
        int size = pageSize == null ? DEFAULT_PAGE_SIZE : Math.max(1, pageSize);
        String normalizedQ = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        BookingStatus statusFilter = parseStatus(status);

        Page<Booking> result = bookingRepository.search(
                statusFilter,
                normalizedQ,
                PageRequest.of(pageNumber - 1, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        return new PageResponse<>(
                result.getContent().stream().map(bookingMapper::toResponse).toList(),
                result.getTotalElements(),
                pageNumber,
                size);
    }

    /** Confirms a pending booking. */
    @Transactional
    public BookingResponse confirm(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
        Instant now = Instant.now();
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setConfirmedAt(now);
        booking.setUpdatedAt(now);
        return bookingMapper.toResponse(booking);
    }

    /** Declines a booking. */
    @Transactional
    public BookingResponse decline(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
        booking.setStatus(BookingStatus.DECLINED);
        booking.setUpdatedAt(Instant.now());
        return bookingMapper.toResponse(booking);
    }

    /** Blank / null / {@code "ALL"} means no status filter; anything else must be a valid status. */
    private BookingStatus parseStatus(String status) {
        if (status == null || status.isBlank() || status.equalsIgnoreCase("ALL")) {
            return null;
        }
        try {
            return BookingStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown booking status: " + status);
        }
    }

    private Optional<BigDecimal> resolvePrice(String fromId, String toId, String vehicleId) {
        return transferPriceRepository
                .findByFromLocation_IdAndToLocation_IdAndVehicle_Id(fromId, toId, vehicleId)
                .map(TransferPrice::getPrice);
    }

    private String uniqueReferenceCode() {
        String code;
        do {
            code = referenceCodeGenerator.generate();
        } while (bookingRepository.existsByReferenceCode(code));
        return code;
    }
}
