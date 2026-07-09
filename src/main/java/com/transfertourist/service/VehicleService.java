package com.transfertourist.service;

import com.transfertourist.dto.request.VehicleRequest;
import com.transfertourist.dto.response.VehicleResponse;
import com.transfertourist.entity.Vehicle;
import com.transfertourist.exception.BusinessRuleException;
import com.transfertourist.exception.ResourceNotFoundException;
import com.transfertourist.mapper.VehicleMapper;
import com.transfertourist.repository.BookingRepository;
import com.transfertourist.repository.TransferPriceRepository;
import com.transfertourist.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Read access (public) and admin CRUD for vehicles. */
@Service
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final TransferPriceRepository transferPriceRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleService(VehicleRepository vehicleRepository,
                          BookingRepository bookingRepository,
                          TransferPriceRepository transferPriceRepository,
                          VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
        this.transferPriceRepository = transferPriceRepository;
        this.vehicleMapper = vehicleMapper;
    }

    /** All active vehicles, ordered by sort order. */
    public List<VehicleResponse> list() {
        return vehicleRepository.findByActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }

    /** Admin: every vehicle (incl. inactive), ordered by sort order. */
    public List<VehicleResponse> adminList() {
        return vehicleRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId("veh-" + UUID.randomUUID());
        vehicleMapper.applyRequest(request, vehicle);
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public VehicleResponse update(String id, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
        vehicleMapper.applyRequest(request, vehicle);
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void delete(String id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));

        // Guard on both bookings (mock) and transfer prices (real FK would block anyway).
        if (bookingRepository.existsByVehicle_Id(id) || transferPriceRepository.existsByVehicle_Id(id)) {
            throw new BusinessRuleException(
                    "This vehicle is used by existing bookings or transfer prices. "
                            + "Deactivate it instead of deleting.");
        }
        vehicleRepository.delete(vehicle);
    }
}
