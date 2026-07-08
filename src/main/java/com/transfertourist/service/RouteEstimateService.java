package com.transfertourist.service;

import com.transfertourist.dto.response.RouteEstimateResponse;
import com.transfertourist.entity.Location;
import com.transfertourist.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Computes a route's distance/duration estimate. Straight-line distance comes
 * from the locations' coordinates (haversine); road distance/duration come from
 * a curated override table where available, else a heuristic. This mirrors the
 * frontend {@code estimateRoute} in {@code handlers.ts} so the sidebar numbers
 * stay identical after the mock -> API swap. Phase 3 replaces this with a Maps API.
 */
@Service
@Transactional(readOnly = true)
public class RouteEstimateService {

    private static final double EARTH_RADIUS_KM = 6371;

    /** Road distance/duration for a route. */
    private record Override(int roadKm, int durationMinutes) {
    }

    /** Curated overrides keyed by an order-independent location pair (see {@link #routeKey}). */
    private static final Map<String, Override> ROUTE_OVERRIDES = Map.of(
            routeKey("loc-skp-air", "loc-ohrid"), new Override(175, 150),
            routeKey("loc-tia-air", "loc-ohrid"), new Override(135, 150),
            routeKey("loc-ohrid", "loc-thessaloniki"), new Override(215, 190),
            routeKey("loc-skopje", "loc-tirana"), new Override(290, 270),
            routeKey("loc-ohrid", "loc-skopje"), new Override(170, 145),
            routeKey("loc-skopje", "loc-thessaloniki"), new Override(230, 165)
    );

    private final LocationRepository locationRepository;

    public RouteEstimateService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public RouteEstimateResponse estimate(String fromId, String toId) {
        Location from = locationRepository.findById(fromId).orElse(null);
        Location to = locationRepository.findById(toId).orElse(null);

        double straight = hasCoordinates(from) && hasCoordinates(to)
                ? haversineKm(from.getLat(), from.getLng(), to.getLat(), to.getLng())
                : 0;

        Override override = ROUTE_OVERRIDES.get(routeKey(fromId, toId));
        int roadKm = override != null ? override.roadKm() : (int) Math.round(straight * 1.3);
        int durationMinutes = override != null
                ? override.durationMinutes()
                : (int) Math.round((roadKm / 75.0) * 60);

        return new RouteEstimateResponse(fromId, toId, (int) Math.round(straight), roadKm, durationMinutes);
    }

    private static boolean hasCoordinates(Location location) {
        return location != null && location.getLat() != null && location.getLng() != null;
    }

    /** Builds an order-independent key for a location pair (mirrors the frontend {@code routeKey}). */
    private static String routeKey(String a, String b) {
        return a.compareTo(b) <= 0 ? a + "__" + b : b + "__" + a;
    }

    private static double haversineKm(double aLat, double aLng, double bLat, double bLng) {
        double dLat = Math.toRadians(bLat - aLat);
        double dLng = Math.toRadians(bLng - aLng);
        double lat1 = Math.toRadians(aLat);
        double lat2 = Math.toRadians(bLat);
        double h = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLng / 2), 2);
        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(h));
    }
}
