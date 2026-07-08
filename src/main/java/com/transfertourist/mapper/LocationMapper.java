package com.transfertourist.mapper;

import com.transfertourist.constants.LocationCategory;
import com.transfertourist.dto.response.LocationResponse;
import com.transfertourist.entity.Location;
import org.springframework.stereotype.Component;

/** Converts {@link Location} entities to their public DTO. */
@Component
public class LocationMapper {

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
