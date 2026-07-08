package com.transfertourist.dto.request;

import com.transfertourist.constants.TripType;
import com.transfertourist.dto.common.CustomerDto;
import com.transfertourist.dto.common.LegDto;
import com.transfertourist.validation.ValidBooking;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload for creating a booking (mirrors the frontend {@code BookingRequestInput}).
 * The always-present return leg is only validated/persisted for RETURN trips.
 * Field-level rules cover the unconditional constraints; {@link ValidBooking}
 * covers the trip-type/purpose-dependent cross-field rules.
 */
@ValidBooking
public record BookingCreateRequest(

        @NotNull TripType tripType,

        @Min(value = 1, message = "At least 1 passenger")
        @Max(value = 60, message = "Too many passengers") int passengers,

        @Min(value = 0, message = "Infant seats cannot be negative")
        @Max(value = 2, message = "Up to 2 infant seats") int infantSeats,

        @Size(max = 1000, message = "Comments are too long") String comments,

        @NotBlank(message = "Please select a vehicle") String vehicleId,

        @NotNull @Valid LegDto outbound,

        @Valid LegDto returnLeg,

        @NotNull @Valid CustomerDto customer
) {
}
