package com.transfertourist.service;

import com.transfertourist.constants.LocationCategory;
import com.transfertourist.dto.response.GroupedLocationsResponse;
import com.transfertourist.dto.response.LocationResponse;
import com.transfertourist.mapper.LocationMapper;
import com.transfertourist.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/** Read access to the location reference data for the public surface. */
@Service
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public LocationService(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
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
}
