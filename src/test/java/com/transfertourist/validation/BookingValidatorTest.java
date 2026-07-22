package com.transfertourist.validation;

import com.transfertourist.constants.TransferPurpose;
import com.transfertourist.constants.TripType;
import com.transfertourist.dto.common.CustomerDto;
import com.transfertourist.dto.common.LegDto;
import com.transfertourist.dto.request.BookingCreateRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests {@link BookingValidator} (the {@code @ValidBooking} cross-field rules,
 * CR-2) through the real Jakarta {@link Validator}, so the constraint wiring and
 * the dotted property paths ({@code outbound.toLocationId}, {@code returnLeg.date})
 * that the SPA attaches to fields are exercised exactly as in production.
 */
class BookingValidatorTest {

    private static final LocalDate OUT_DATE = LocalDate.of(2026, 8, 1);
    private static final LocalDate LATER_DATE = LocalDate.of(2026, 8, 5);

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    // ----------------------------------------------------------------- happy paths

    @Test
    void validOneWayBookingHasNoViolations() {
        Set<ConstraintViolation<BookingCreateRequest>> violations =
                validator.validate(oneWay(notFlyingLeg("loc-a", "loc-b", OUT_DATE, "09:00")));

        assertThat(violations).isEmpty();
    }

    @Test
    void validReturnBookingHasNoViolations() {
        Set<ConstraintViolation<BookingCreateRequest>> violations = validator.validate(returnTrip(
                notFlyingLeg("loc-a", "loc-b", OUT_DATE, "09:00"),
                notFlyingLeg("loc-b", "loc-a", LATER_DATE, "09:00")));

        assertThat(violations).isEmpty();
    }

    @Test
    void sameDayReturnWithLaterTimeIsAllowed() {
        Set<ConstraintViolation<BookingCreateRequest>> violations = validator.validate(returnTrip(
                notFlyingLeg("loc-a", "loc-b", OUT_DATE, "09:00"),
                notFlyingLeg("loc-b", "loc-a", OUT_DATE, "18:00")));

        assertThat(violations).isEmpty();
    }

    @Test
    void oneWayTripIgnoresAnInvalidReturnLeg() {
        // A malformed return leg (same from/to) must not error on a one-way trip.
        BookingCreateRequest req = new BookingCreateRequest(TripType.ONE_WAY, 2, 0, null, "veh-1",
                notFlyingLeg("loc-a", "loc-b", OUT_DATE, "09:00"),
                notFlyingLeg("loc-x", "loc-x", OUT_DATE, "09:00"),
                customer());

        Set<ConstraintViolation<BookingCreateRequest>> violations = validator.validate(req);

        // No violations at all — in particular nothing under returnLeg, which the
        // validator skips entirely for a one-way trip.
        assertThat(violations).isEmpty();
    }

    // ----------------------------------------------------------------- leg rules

    @Test
    void rejectsSameFromAndToOnOutbound() {
        Set<ConstraintViolation<BookingCreateRequest>> violations =
                validator.validate(oneWay(notFlyingLeg("loc-a", "loc-a", OUT_DATE, "09:00")));

        assertThat(hasViolation(violations, "outbound.toLocationId", "different from pickup")).isTrue();
    }

    @Test
    void reportsMissingOutboundLocations() {
        Set<ConstraintViolation<BookingCreateRequest>> violations =
                validator.validate(oneWay(notFlyingLeg(null, "  ", OUT_DATE, "09:00")));

        assertThat(hasViolation(violations, "outbound.fromLocationId", "pickup location")).isTrue();
        assertThat(hasViolation(violations, "outbound.toLocationId", "destination")).isTrue();
    }

    @Test
    void reportsMissingOutboundDate() {
        Set<ConstraintViolation<BookingCreateRequest>> violations =
                validator.validate(oneWay(notFlyingLeg("loc-a", "loc-b", null, "09:00")));

        assertThat(hasViolation(violations, "outbound.date", "Select a date")).isTrue();
    }

    @Test
    void arrivingLegRequiresFlightNumberAndTime() {
        LegDto arriving = new LegDto("loc-a", "loc-b", OUT_DATE, TransferPurpose.ARRIVING,
                null, null, null);

        Set<ConstraintViolation<BookingCreateRequest>> violations = validator.validate(oneWay(arriving));

        assertThat(hasViolation(violations, "outbound.flightNumber", "flight number")).isTrue();
        assertThat(hasViolation(violations, "outbound.flightTime", "arrival time")).isTrue();
    }

    @Test
    void departingLegRequiresPickupAndFlightTime() {
        LegDto departing = new LegDto("loc-a", "loc-b", OUT_DATE, TransferPurpose.DEPARTING,
                null, null, null);

        Set<ConstraintViolation<BookingCreateRequest>> violations = validator.validate(oneWay(departing));

        assertThat(hasViolation(violations, "outbound.pickupTime", "pickup time")).isTrue();
        assertThat(hasViolation(violations, "outbound.flightTime", "departure time")).isTrue();
    }

    // ----------------------------------------------------------------- return ordering

    @Test
    void rejectsReturnDateBeforeOutbound() {
        Set<ConstraintViolation<BookingCreateRequest>> violations = validator.validate(returnTrip(
                notFlyingLeg("loc-a", "loc-b", LATER_DATE, "09:00"),
                notFlyingLeg("loc-b", "loc-a", OUT_DATE, "09:00")));

        assertThat(hasViolation(violations, "returnLeg.date", "on or after the outbound date")).isTrue();
    }

    @Test
    void rejectsSameDayReturnEarlierThanOutbound() {
        Set<ConstraintViolation<BookingCreateRequest>> violations = validator.validate(returnTrip(
                notFlyingLeg("loc-a", "loc-b", OUT_DATE, "09:00"),
                notFlyingLeg("loc-b", "loc-a", OUT_DATE, "07:00")));

        assertThat(hasViolation(violations, "returnLeg.date", "after the outbound time")).isTrue();
    }

    // ----------------------------------------------------------------- fixtures

    private static boolean hasViolation(Set<ConstraintViolation<BookingCreateRequest>> violations,
                                        String path, String messagePart) {
        return violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals(path) && v.getMessage().contains(messagePart));
    }

    private LegDto notFlyingLeg(String from, String to, LocalDate date, String pickupTime) {
        return new LegDto(from, to, date, TransferPurpose.NOT_FLYING, null, null, pickupTime);
    }

    private CustomerDto customer() {
        return new CustomerDto("Test", "User", "tomevskihristijan97@gmail.com", "+38970000000");
    }

    private BookingCreateRequest oneWay(LegDto outbound) {
        return new BookingCreateRequest(TripType.ONE_WAY, 2, 0, null, "veh-1", outbound, null, customer());
    }

    private BookingCreateRequest returnTrip(LegDto outbound, LegDto returnLeg) {
        return new BookingCreateRequest(TripType.RETURN, 2, 0, null, "veh-1", outbound, returnLeg, customer());
    }
}
