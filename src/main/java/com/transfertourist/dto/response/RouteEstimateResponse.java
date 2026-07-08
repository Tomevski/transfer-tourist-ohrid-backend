package com.transfertourist.dto.response;

/**
 * Distance/duration estimate for a route, shown in the booking sidebar. Mirrors
 * the frontend {@code RouteEstimate} type. Phase 1/2 uses a haversine estimate
 * plus a curated override table; Phase 3 will source these from a Maps API.
 */
public record RouteEstimateResponse(
        String fromId,
        String toId,
        int distanceKm,
        int roadKm,
        int durationMinutes
) {
}
