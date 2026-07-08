package com.transfertourist.dto.response;

import com.transfertourist.constants.LocationCategory;

/**
 * Public representation of a {@code location}. Field names/shape mirror the
 * frontend {@code Location} type so the mock -> API swap is zero-touch.
 */
public record LocationResponse(
        String id,
        String name,
        String slug,
        LocationCategory category,
        String city,
        String country,
        Double lat,
        Double lng,
        boolean active,
        int sortOrder
) {
}
