package com.transfertourist.controller;

import com.transfertourist.dto.response.LocationResponse;
import com.transfertourist.service.LocationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin location list (includes inactive), separate from the public
 * {@code /locations} path so the SPA can manage every row. {@code ROLE_ADMIN}
 * only, per the security matrix.
 */
@RestController
@RequestMapping("/api/v1/admin/locations")
public class AdminLocationController {

    private final LocationService locationService;

    public AdminLocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public List<LocationResponse> list() {
        return locationService.adminList();
    }
}
