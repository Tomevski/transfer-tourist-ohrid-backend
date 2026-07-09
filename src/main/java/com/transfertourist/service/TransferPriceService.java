package com.transfertourist.service;

import com.transfertourist.dto.request.TransferPriceRequest;
import com.transfertourist.dto.response.TransferPriceResponse;
import com.transfertourist.entity.Location;
import com.transfertourist.entity.TransferPrice;
import com.transfertourist.entity.Vehicle;
import com.transfertourist.exception.BusinessRuleException;
import com.transfertourist.exception.ResourceNotFoundException;
import com.transfertourist.mapper.TransferPriceMapper;
import com.transfertourist.repository.LocationRepository;
import com.transfertourist.repository.TransferPriceRepository;
import com.transfertourist.repository.VehicleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Read access (public) and admin CRUD for route pricing. */
@Service
@Transactional(readOnly = true)
public class TransferPriceService {

    private final TransferPriceRepository transferPriceRepository;
    private final LocationRepository locationRepository;
    private final VehicleRepository vehicleRepository;
    private final TransferPriceMapper transferPriceMapper;

    public TransferPriceService(TransferPriceRepository transferPriceRepository,
                                LocationRepository locationRepository,
                                VehicleRepository vehicleRepository,
                                TransferPriceMapper transferPriceMapper) {
        this.transferPriceRepository = transferPriceRepository;
        this.locationRepository = locationRepository;
        this.vehicleRepository = vehicleRepository;
        this.transferPriceMapper = transferPriceMapper;
    }

    /** Transfer prices (one per priced vehicle) defined for a route direction. */
    public List<TransferPriceResponse> byRoute(String fromLocationId, String toLocationId) {
        return transferPriceRepository
                .findByFromLocation_IdAndToLocation_Id(fromLocationId, toLocationId)
                .stream()
                .map(transferPriceMapper::toResponse)
                .toList();
    }

    /** Admin: every transfer price. */
    public List<TransferPriceResponse> adminList() {
        return transferPriceRepository.findAll(Sort.by("id"))
                .stream()
                .map(transferPriceMapper::toResponse)
                .toList();
    }

    @Transactional
    public TransferPriceResponse create(TransferPriceRequest request) {
        validateDistinctRoute(request);
        requireNoDuplicate(request, null);

        TransferPrice tp = new TransferPrice();
        tp.setId("tp-" + UUID.randomUUID());
        applyRequest(request, tp);
        return transferPriceMapper.toResponse(transferPriceRepository.save(tp));
    }

    @Transactional
    public TransferPriceResponse update(String id, TransferPriceRequest request) {
        TransferPrice tp = transferPriceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer price", id));
        validateDistinctRoute(request);
        requireNoDuplicate(request, id);

        applyRequest(request, tp);
        return transferPriceMapper.toResponse(transferPriceRepository.save(tp));
    }

    @Transactional
    public void delete(String id) {
        TransferPrice tp = transferPriceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer price", id));
        transferPriceRepository.delete(tp);
    }

    private void validateDistinctRoute(TransferPriceRequest request) {
        if (request.fromLocationId().equals(request.toLocationId())) {
            throw new IllegalArgumentException("From and To must be different");
        }
    }

    /** Rejects a second price for the same (from, to, vehicle), ignoring the row being updated. */
    private void requireNoDuplicate(TransferPriceRequest request, String ignoreId) {
        transferPriceRepository
                .findByFromLocation_IdAndToLocation_IdAndVehicle_Id(
                        request.fromLocationId(), request.toLocationId(), request.vehicleId())
                .filter(existing -> !existing.getId().equals(ignoreId))
                .ifPresent(existing -> {
                    throw new BusinessRuleException("A price for this route and vehicle already exists");
                });
    }

    /** Resolves the location/vehicle references (404 if any is missing) and sets the price. */
    private void applyRequest(TransferPriceRequest request, TransferPrice tp) {
        Location from = locationRepository.findById(request.fromLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location", request.fromLocationId()));
        Location to = locationRepository.findById(request.toLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location", request.toLocationId()));
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", request.vehicleId()));

        tp.setFromLocation(from);
        tp.setToLocation(to);
        tp.setVehicle(vehicle);
        tp.setPrice(request.price());
    }
}
