package com.transfertourist.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Admin create/update payload for a vehicle (mirrors the frontend
 * {@code VehicleInput = Omit<Vehicle, 'id'>}).
 */
public record VehicleRequest(
        @NotBlank(message = "Name is required") @Size(max = 120) String name,
        @Min(value = 1, message = "Capacity must be at least 1") int capacity,
        @NotNull @Size(max = 500) String description,
        @NotNull List<@Size(max = 120) String> features,
        @NotNull @Size(max = 255) String imageUrl,
        boolean active,
        int sortOrder
) {
}
