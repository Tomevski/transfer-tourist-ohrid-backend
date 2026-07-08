package com.transfertourist.dto.response;

import com.transfertourist.constants.BookingStatus;
import com.transfertourist.constants.TripType;
import com.transfertourist.dto.common.CustomerDto;
import com.transfertourist.dto.common.LegDto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Booking as returned after creation (and to the admin). Mirrors the frontend
 * {@code BookingRequest} type: the submitted input plus the server-assigned
 * {@code id}, {@code referenceCode}, {@code status}, authoritative
 * {@code totalPrice} and {@code createdAt}. {@code returnLeg} is null for one-way.
 */
public record BookingResponse(
        String id,
        String referenceCode,
        BookingStatus status,
        TripType tripType,
        int passengers,
        int infantSeats,
        String comments,
        String vehicleId,
        BigDecimal totalPrice,
        LegDto outbound,
        LegDto returnLeg,
        CustomerDto customer,
        Instant createdAt
) {
}
