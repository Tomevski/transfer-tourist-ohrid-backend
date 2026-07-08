package com.transfertourist.dto.response;

import com.transfertourist.constants.LocationCategory;

import java.util.List;

/**
 * Locations grouped by category for the Locations page. Mirrors the frontend
 * {@code GroupedLocations} type ({@code category}, display {@code label},
 * {@code locations}).
 */
public record GroupedLocationsResponse(
        LocationCategory category,
        String label,
        List<LocationResponse> locations
) {
}
