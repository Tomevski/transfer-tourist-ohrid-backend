package com.transfertourist.controller;

import com.transfertourist.dto.response.VehicleResponse;
import com.transfertourist.service.VehicleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Admin vehicle list (includes inactive). {@code ROLE_ADMIN} only. */
@RestController
@RequestMapping("/api/v1/admin/vehicles")
public class AdminVehicleController {

    private final VehicleService vehicleService;

    public AdminVehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<VehicleResponse> list() {
        return vehicleService.adminList();
    }
}
