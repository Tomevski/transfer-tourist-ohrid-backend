package com.transfertourist.service;

import com.transfertourist.constants.BookingStatus;
import com.transfertourist.constants.TransferPurpose;
import com.transfertourist.constants.TripType;
import com.transfertourist.dto.common.CustomerDto;
import com.transfertourist.dto.common.LegDto;
import com.transfertourist.dto.common.PageResponse;
import com.transfertourist.dto.request.BookingCreateRequest;
import com.transfertourist.dto.response.BookingResponse;
import com.transfertourist.entity.Booking;
import com.transfertourist.entity.Customer;
import com.transfertourist.entity.Leg;
import com.transfertourist.entity.TransferPrice;
import com.transfertourist.entity.Vehicle;
import com.transfertourist.event.BookingConfirmedEvent;
import com.transfertourist.event.BookingCreatedEvent;
import com.transfertourist.event.BookingDeclinedEvent;
import com.transfertourist.exception.BusinessRuleException;
import com.transfertourist.exception.ResourceNotFoundException;
import com.transfertourist.mapper.BookingMapper;
import com.transfertourist.repository.BookingRepository;
import com.transfertourist.repository.LocationRepository;
import com.transfertourist.repository.TransferPriceRepository;
import com.transfertourist.repository.VehicleRepository;
import com.transfertourist.util.ReferenceCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BookingService} — the server-authoritative pricing and
 * business rules (CR-2). Uses the real {@link BookingMapper} and real entities so
 * the actual fare math is exercised; only the repositories and collaborators are
 * mocked. The client-sent total is not part of the request, so these tests are
 * the guard that the persisted total is computed, never trusted.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private static final String VEHICLE_ID = "veh-1";
    private static final String FROM = "loc-a";
    private static final String TO = "loc-b";
    private static final LocalDate TRIP_DATE = LocalDate.of(2026, 8, 1);

    @Mock private BookingRepository bookingRepository;
    @Mock private VehicleRepository vehicleRepository;
    @Mock private TransferPriceRepository transferPriceRepository;
    @Mock private LocationRepository locationRepository;
    @Mock private SettingService settingService;
    @Mock private ReferenceCodeGenerator referenceCodeGenerator;
    @Mock private ApplicationEventPublisher eventPublisher;

    private BookingService service;

    @BeforeEach
    void setUp() {
        service = new BookingService(bookingRepository, vehicleRepository,
                transferPriceRepository, locationRepository, settingService,
                new BookingMapper(), referenceCodeGenerator, eventPublisher);
    }

    // ----------------------------------------------------------------- create: pricing

    @Test
    void oneWayTotalIsOutboundPriceWhenNoInfantSeats() {
        stubVehicle(capacity(4));
        stubRoute(FROM, TO, "25.00");
        stubSettingsAndPersistence("10");

        BookingResponse response = service.create(oneWay(0, 2));

        assertThat(response.totalPrice()).isEqualByComparingTo("25");
        assertThat(response.status()).isEqualTo(BookingStatus.PENDING);
        assertThat(response.returnLeg()).isNull();
    }

    @Test
    void oneWayChargesInfantSeatsOncePerLeg() {
        stubVehicle(capacity(4));
        stubRoute(FROM, TO, "25.00");
        stubSettingsAndPersistence("10");

        // 25 outbound + 2 infants × €10 × 1 leg = 45
        BookingResponse response = service.create(oneWay(2, 2));

        assertThat(response.totalPrice()).isEqualByComparingTo("45");
    }

    @Test
    void returnTotalSumsBothLegsAndChargesInfantsPerLeg() {
        stubVehicle(capacity(4));
        stubRoute(FROM, TO, "25.00");
        stubRoute(TO, FROM, "30.00");
        stubSettingsAndPersistence("10");

        // 25 outbound + 30 return + 2 infants × €10 × 2 legs = 95
        BookingResponse response = service.create(returnTrip(2, 2));

        assertThat(response.totalPrice()).isEqualByComparingTo("95");
    }

    @Test
    void returnLegFallsBackToOutboundPriceWhenReverseRouteUnpriced() {
        stubVehicle(capacity(4));
        stubRoute(FROM, TO, "25.00");
        when(transferPriceRepository
                .findByFromLocation_IdAndToLocation_IdAndVehicle_Id(TO, FROM, VEHICLE_ID))
                .thenReturn(Optional.empty());
        stubSettingsAndPersistence("10");

        // 25 outbound + 25 (fallback) + 1 infant × €10 × 2 legs = 70
        BookingResponse response = service.create(returnTrip(1, 2));

        assertThat(response.totalPrice()).isEqualByComparingTo("70");
    }

    // ----------------------------------------------------------------- create: rules

    @Test
    void rejectsBookingWhenPassengersExceedVehicleCapacity() {
        stubVehicle(capacity(4));

        assertThatExceptionOfType(BusinessRuleException.class)
                .isThrownBy(() -> service.create(oneWay(0, 5)));

        verify(bookingRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void rejectsBookingWhenVehicleDoesNotExist() {
        when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> service.create(oneWay(0, 2)));
    }

    @Test
    void rejectsBookingWhenRouteHasNoConfiguredPrice() {
        stubVehicle(capacity(4));
        when(transferPriceRepository
                .findByFromLocation_IdAndToLocation_IdAndVehicle_Id(FROM, TO, VEHICLE_ID))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(BusinessRuleException.class)
                .isThrownBy(() -> service.create(oneWay(0, 2)));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void publishesBookingCreatedEventOnSuccess() {
        stubVehicle(capacity(4));
        stubRoute(FROM, TO, "25.00");
        stubSettingsAndPersistence("10");

        service.create(oneWay(0, 2));

        verify(eventPublisher).publishEvent(any(BookingCreatedEvent.class));
    }

    @Test
    void retriesReferenceCodeUntilUnique() {
        stubVehicle(capacity(4));
        stubRoute(FROM, TO, "25.00");
        when(settingService.infantSeatPrice()).thenReturn(new BigDecimal("10"));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
        when(referenceCodeGenerator.generate()).thenReturn("TT-DUP11", "TT-OK123");
        when(bookingRepository.existsByReferenceCode("TT-DUP11")).thenReturn(true);
        when(bookingRepository.existsByReferenceCode("TT-OK123")).thenReturn(false);

        BookingResponse response = service.create(oneWay(0, 2));

        assertThat(response.referenceCode()).isEqualTo("TT-OK123");
    }

    // ----------------------------------------------------------------- confirm / decline

    @Test
    void confirmSetsStatusConfirmedAndPublishesEvent() {
        Booking booking = persistedBooking();
        when(bookingRepository.findById("bkg-1")).thenReturn(Optional.of(booking));

        BookingResponse response = service.confirm("bkg-1");

        assertThat(response.status()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(booking.getConfirmedAt()).isNotNull();
        verify(eventPublisher).publishEvent(any(BookingConfirmedEvent.class));
    }

    @Test
    void declineSetsStatusDeclinedAndPublishesEvent() {
        Booking booking = persistedBooking();
        when(bookingRepository.findById("bkg-1")).thenReturn(Optional.of(booking));

        BookingResponse response = service.decline("bkg-1");

        assertThat(response.status()).isEqualTo(BookingStatus.DECLINED);
        verify(eventPublisher).publishEvent(any(BookingDeclinedEvent.class));
    }

    @Test
    void confirmRejectsUnknownBooking() {
        when(bookingRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> service.confirm("missing"));
        verify(eventPublisher, never()).publishEvent(any());
    }

    // ----------------------------------------------------------------- adminList

    @Test
    void adminListRejectsUnknownStatusFilter() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> service.adminList("BOGUS", null, null, null));
    }

    @Test
    void adminListDefaultsToFirstPageOfTenAndMapsResults() {
        Booking booking = persistedBooking();
        when(bookingRepository.search(any(), anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking), PageRequest.of(0, 10), 1));

        PageResponse<BookingResponse> page = service.adminList(null, null, null, null);

        assertThat(page.page()).isEqualTo(1);
        assertThat(page.pageSize()).isEqualTo(10);
        assertThat(page.total()).isEqualTo(1);
        assertThat(page.items()).hasSize(1);
    }

    @Test
    void adminListTreatsAllAsNoStatusFilter() {
        when(bookingRepository.search(eq(null), anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        service.adminList("ALL", null, null, null);

        // null status arg = no filter; the eq(null) stub above asserts it.
        verify(bookingRepository).search(eq(null), anyString(), any(Pageable.class));
    }

    // ----------------------------------------------------------------- fixtures

    private void stubVehicle(Vehicle vehicle) {
        when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(vehicle));
    }

    private void stubRoute(String from, String to, String price) {
        when(transferPriceRepository
                .findByFromLocation_IdAndToLocation_IdAndVehicle_Id(from, to, VEHICLE_ID))
                .thenReturn(Optional.of(transferPrice(price)));
    }

    /** Common stubs for a create that reaches persistence: pricing, ref-code, save. */
    private void stubSettingsAndPersistence(String infantPrice) {
        when(settingService.infantSeatPrice()).thenReturn(new BigDecimal(infantPrice));
        when(referenceCodeGenerator.generate()).thenReturn("TT-ABC123");
        when(bookingRepository.existsByReferenceCode("TT-ABC123")).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Vehicle capacity(int capacity) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(VEHICLE_ID);
        vehicle.setName("Sedan");
        vehicle.setCapacity(capacity);
        return vehicle;
    }

    private TransferPrice transferPrice(String price) {
        TransferPrice tp = new TransferPrice();
        tp.setPrice(new BigDecimal(price));
        return tp;
    }

    private LegDto leg(String from, String to) {
        return new LegDto(from, to, TRIP_DATE, TransferPurpose.ARRIVING, null, null, "10:00");
    }

    private CustomerDto customer() {
        return new CustomerDto("Test", "User", "tomevskihristijan97@gmail.com", "+38970000000");
    }

    private BookingCreateRequest oneWay(int infantSeats, int passengers) {
        return new BookingCreateRequest(TripType.ONE_WAY, passengers, infantSeats, null,
                VEHICLE_ID, leg(FROM, TO), null, customer());
    }

    private BookingCreateRequest returnTrip(int infantSeats, int passengers) {
        return new BookingCreateRequest(TripType.RETURN, passengers, infantSeats, null,
                VEHICLE_ID, leg(FROM, TO), leg(TO, FROM), customer());
    }

    /** A persisted, PENDING one-way booking as the confirm/decline/list paths expect. */
    private Booking persistedBooking() {
        Customer c = new Customer();
        c.setFirstName("Test");
        c.setLastName("User");
        c.setEmail("tomevskihristijan97@gmail.com");
        c.setPhone("+38970000000");

        Leg outbound = new Leg();
        outbound.setFromLocationId(FROM);
        outbound.setToLocationId(TO);
        outbound.setDate(TRIP_DATE);
        outbound.setPurpose(TransferPurpose.ARRIVING);

        Booking booking = new Booking();
        booking.setId("bkg-1");
        booking.setReferenceCode("TT-ABC123");
        booking.setStatus(BookingStatus.PENDING);
        booking.setTripType(TripType.ONE_WAY);
        booking.setPassengers(2);
        booking.setInfantSeats(0);
        booking.setVehicle(capacity(4));
        booking.setTotalPrice(new BigDecimal("25.00"));
        booking.setCustomer(c);
        booking.setOutbound(outbound);
        return booking;
    }
}
