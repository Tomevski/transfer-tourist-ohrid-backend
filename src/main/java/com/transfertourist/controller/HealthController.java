package com.transfertourist.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Minimal liveness endpoint proving the API is up and the {@code /api/v1} base
 * path is wired. Actuator's {@code /actuator/health} covers deeper checks.
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "status", "ok",
                "service", "transfer-tourist",
                "time", Instant.now().toString()
        );
    }
}
