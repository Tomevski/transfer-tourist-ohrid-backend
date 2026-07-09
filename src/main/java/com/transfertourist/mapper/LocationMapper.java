package com.transfertourist.mapper;

import com.transfertourist.constants.LocationCategory;
import com.transfertourist.dto.request.LocationRequest;
import com.transfertourist.dto.response.LocationResponse;
import com.transfertourist.entity.Location;
import org.springframework.stereotype.Component;

/** Converts {@link Location} entities to their public DTO. */
@Component
public class LocationMapper {

    /**
     * Copies the mutable fields from an admin request onto an entity. The
     * {@code id} and {@code slug} are managed by the service (slug is derived
     * from the name), so they are deliberately not touched here.
     */
    public void applyRequest(LocationRequest request, Location location) {
        location.setName(request.name().trim());
        location.setCategory(request.category());
        location.setCity(request.city());
        location.setCountry(request.country());
        location.setLat(request.lat());
        location.setLng(request.lng());
        location.setActive(request.active());
        location.setSortOrder(request.sortOrder());
    }

    public LocationResponse toResponse(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getSlug(),
                location.getCategory(),
                location.getCity(),
                location.getCountry(),
                location.getLat(),
                location.getLng(),
                location.isActive(),
                location.getSortOrder()
        );
    }

    /** Human-friendly plural label for a category, used by the grouped view. */
    public String label(LocationCategory category) {
        return switch (category) {
            case AIRPORT -> "Airports";
            case CITY -> "Cities";
            case HOTEL -> "Hotels";
            case RESORT -> "Resorts";
        };
    }
}
