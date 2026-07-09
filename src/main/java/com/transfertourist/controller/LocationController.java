package com.transfertourist.controller;

import com.transfertourist.dto.common.IdResponse;
import com.transfertourist.dto.request.LocationRequest;
import com.transfertourist.dto.response.GroupedLocationsResponse;
import com.transfertourist.dto.response.LocationResponse;
import com.transfertourist.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Locations: public reads plus admin mutations. The list is served from
 * {@code /admin/locations} (see {@link AdminLocationController}); create/update/
 * delete live here under the resource path and are gated to {@code ROLE_ADMIN}
 * by the security matrix.
 */
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationResponse create(@Valid @RequestBody LocationRequest request) {
        return locationService.create(request);
    }

    @PutMapping("/{id}")
    public LocationResponse update(@PathVariable String id, @Valid @RequestBody LocationRequest request) {
        return locationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public IdResponse delete(@PathVariable String id) {
        locationService.delete(id);
        return new IdResponse(id);
    }
}
