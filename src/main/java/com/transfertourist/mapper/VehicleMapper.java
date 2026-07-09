package com.transfertourist.mapper;

import com.transfertourist.dto.request.VehicleRequest;
import com.transfertourist.dto.response.VehicleResponse;
import com.transfertourist.entity.Vehicle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Converts {@link Vehicle} entities to their public DTO. */
@Component
public class VehicleMapper {

    /** Copies mutable fields from an admin request onto an entity ({@code id} is managed by the service). */
    public void applyRequest(VehicleRequest request, Vehicle vehicle) {
        vehicle.setName(request.name().trim());
        vehicle.setCapacity(request.capacity());
        vehicle.setDescription(request.description());
        vehicle.setFeatures(new ArrayList<>(request.features()));
        vehicle.setImageUrl(request.imageUrl());
        vehicle.setActive(request.active());
        vehicle.setSortOrder(request.sortOrder());
    }

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
