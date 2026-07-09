package com.transfertourist.dto.request;

import com.transfertourist.constants.LocationCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Admin create/update payload for a location (mirrors the frontend
 * {@code LocationInput = Omit<Location, 'id' | 'slug'>}). The slug is derived
 * server-side from the name, never sent by the client.
 */
public record LocationRequest(
        @NotBlank(message = "Name is required") @Size(max = 160) String name,
        @NotNull(message = "Category is required") LocationCategory category,
        @Size(max = 120) String city,
        @NotBlank(message = "Country is required") @Size(max = 120) String country,
        @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0") Double lat,
        @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0") Double lng,
        boolean active,
        int sortOrder
) {
}
