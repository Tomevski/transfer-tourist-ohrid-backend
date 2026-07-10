package com.transfertourist.event;

import com.transfertourist.constants.BookingStatus;
import com.transfertourist.constants.TripType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Fully-resolved, immutable snapshot of a booking for email rendering.
 *
 * <p>Assembled inside the {@code @Transactional} service method while the
 * persistence session is open, so the async email listener never touches the
 * {@link com.transfertourist.entity.Booking} entity, its {@code LAZY} vehicle
 * association, or the opaque location ids on each leg. In particular the leg
 * {@code from}/{@code to} are already resolved to human-readable location
 * <b>names</b> and the vehicle to its name — templates render names, never ids.
 */
public record BookingEmailPayload(
        String referenceCode,
        BookingStatus status,
        TripType tripType,
        int passengers,
        int infantSeats,
        String comments,
        String vehicleName,
        BigDecimal totalPrice,
        LegSummary outbound,
        /** {@code null} for a one-way booking. */
        LegSummary returnLeg,
        CustomerSummary customer
) {

    /** A single directional leg with location <b>names</b> (not ids). */
    public record LegSummary(
            String fromLocationName,
            String toLocationName,
            LocalDate date,
            String purposeLabel,
            String flightNumber,
            String flightTime,
            String pickupTime
    ) {
    }

    /** Customer contact details echoed into the emails. */
    public record CustomerSummary(
            String fullName,
            String firstName,
            String email,
            String phone
    ) {
    }
}
