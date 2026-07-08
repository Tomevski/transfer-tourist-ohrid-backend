package com.transfertourist.dto.common;

import com.transfertourist.constants.TransferPurpose;

import java.time.LocalDate;

/**
 * A single directional leg, shared by the booking request and response. Field
 * rules are intentionally lenient here (the always-present return leg must not
 * error on one-way trips) — conditional requiredness lives in the class-level
 * {@code @ValidBooking} validator, mirroring the frontend {@code legShape}.
 */
public record LegDto(
        String fromLocationId,
        String toLocationId,
        LocalDate date,
        TransferPurpose purpose,
        String flightNumber,
        String flightTime,
        String pickupTime
) {
}
