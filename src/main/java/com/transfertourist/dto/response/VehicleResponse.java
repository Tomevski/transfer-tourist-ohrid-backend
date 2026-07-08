package com.transfertourist.dto.response;

import java.util.List;

/**
 * Public representation of a {@code vehicle}. Carries no price — pricing is
 * resolved per route via transfer prices. Mirrors the frontend {@code Vehicle}.
 */
public record VehicleResponse(
        String id,
        String name,
        int capacity,
        String description,
        List<String> features,
        String imageUrl,
        boolean active,
        int sortOrder
) {
}
