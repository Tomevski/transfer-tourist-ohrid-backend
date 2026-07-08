package com.transfertourist.controller;

import com.transfertourist.dto.response.PricingSettingsResponse;
import com.transfertourist.service.SettingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public pricing settings. Admin update of pricing constants arrives in Milestone 2.5. */
@RestController
@RequestMapping("/api/v1/settings")
public class SettingController {

    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("/pricing")
    public PricingSettingsResponse pricing() {
        return settingService.getPricing();
    }
}
