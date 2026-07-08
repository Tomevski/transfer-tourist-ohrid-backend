package com.transfertourist.service;

import com.transfertourist.dto.response.VehicleResponse;
import com.transfertourist.mapper.VehicleMapper;
import com.transfertourist.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Read access to the vehicle fleet for the public surface. */
@Service
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    /** All active vehicles, ordered by sort order. */
    public List<VehicleResponse> list() {
        return vehicleRepository.findByActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }
}
