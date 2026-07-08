package com.transfertourist.controller;

import com.transfertourist.dto.response.GroupedLocationsResponse;
import com.transfertourist.dto.response.LocationResponse;
import com.transfertourist.service.LocationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Public read endpoints for locations. Admin CRUD arrives in Milestone 2.5. */
@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public List<LocationResponse> list() {
        return locationService.list();
    }

    @GetMapping("/grouped")
    public List<GroupedLocationsResponse> grouped() {
        return locationService.grouped();
    }
}
