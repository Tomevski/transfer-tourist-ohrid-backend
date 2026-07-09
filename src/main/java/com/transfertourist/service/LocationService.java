package com.transfertourist.service;

import com.transfertourist.constants.LocationCategory;
import com.transfertourist.dto.request.LocationRequest;
import com.transfertourist.dto.response.GroupedLocationsResponse;
import com.transfertourist.dto.response.LocationResponse;
import com.transfertourist.entity.Location;
import com.transfertourist.exception.BusinessRuleException;
import com.transfertourist.exception.ResourceNotFoundException;
import com.transfertourist.mapper.LocationMapper;
import com.transfertourist.repository.BookingRepository;
import com.transfertourist.repository.LocationRepository;
import com.transfertourist.repository.TransferPriceRepository;
import com.transfertourist.util.Slugifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/** Read access (public) and admin CRUD for location reference data. */
@Service
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;
    private final TransferPriceRepository transferPriceRepository;
    private final BookingRepository bookingRepository;
    private final LocationMapper locationMapper;
    private final Slugifier slugifier;

    public LocationService(LocationRepository locationRepository,
                           TransferPriceRepository transferPriceRepository,
                           BookingRepository bookingRepository,
                           LocationMapper locationMapper,
                           Slugifier slugifier) {
        this.locationRepository = locationRepository;
        this.transferPriceRepository = transferPriceRepository;
        this.bookingRepository = bookingRepository;
        this.locationMapper = locationMapper;
        this.slugifier = slugifier;
    }

    /** All active locations, ordered by sort order. */
    public List<LocationResponse> list() {
        return locationRepository.findByActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(locationMapper::toResponse)
                .toList();
    }

    /** Active locations grouped by category (one group per category, in enum order). */
    public List<GroupedLocationsResponse> grouped() {
        return Arrays.stream(LocationCategory.values())
                .map(category -> new GroupedLocationsResponse(
                        category,
                        locationMapper.label(category),
                        locationRepository.findByCategoryAndActiveTrueOrderBySortOrderAsc(category)
                                .stream()
                                .map(locationMapper::toResponse)
                                .toList()
                ))
                .toList();
    }

    /** Admin: every location (incl. inactive), ordered by category then sort order. */
    public List<LocationResponse> adminList() {
        return locationRepository.findAllByOrderByCategoryAscSortOrderAsc()
                .stream()
                .map(locationMapper::toResponse)
                .toList();
    }

    @Transactional
    public LocationResponse create(LocationRequest request) {
        Location location = new Location();
        location.setId("loc-" + UUID.randomUUID());
        locationMapper.applyRequest(request, location);
        location.setSlug(slugifier.slugify(location.getName()));
        return locationMapper.toResponse(locationRepository.save(location));
    }

    @Transactional
    public LocationResponse update(String id, LocationRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", id));
        locationMapper.applyRequest(request, location);
        location.setSlug(slugifier.slugify(location.getName()));
        return locationMapper.toResponse(locationRepository.save(location));
    }

    @Transactional
    public void delete(String id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", id));

        boolean usedByPrice = transferPriceRepository.existsByFromLocation_IdOrToLocation_Id(id, id);
        boolean usedByBooking = bookingRepository.existsByAnyLegLocation(id);
        if (usedByPrice || usedByBooking) {
            throw new BusinessRuleException(
                    "This location is used by existing bookings or transfer prices. "
                            + "Deactivate it instead of deleting.");
        }
        locationRepository.delete(location);
    }
}
