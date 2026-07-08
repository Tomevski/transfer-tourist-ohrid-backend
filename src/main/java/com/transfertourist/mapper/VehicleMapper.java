package com.transfertourist.mapper;

import com.transfertourist.dto.response.VehicleResponse;
import com.transfertourist.entity.Vehicle;
import org.springframework.stereotype.Component;

import java.util.List;

/** Converts {@link Vehicle} entities to their public DTO. */
@Component
public class VehicleMapper {

    public VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getName(),
                vehicle.getCapacity(),
                vehicle.getDescription(),
                List.copyOf(vehicle.getFeatures()),
                vehicle.getImageUrl(),
                vehicle.isActive(),
                vehicle.getSortOrder()
        );
    }
}
