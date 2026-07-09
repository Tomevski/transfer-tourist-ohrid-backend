package com.transfertourist.controller;

import com.transfertourist.dto.response.StatSummaryResponse;
import com.transfertourist.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Admin dashboard statistics. {@code ROLE_ADMIN} only, per the security matrix. */
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public StatSummaryResponse summary() {
        return statisticsService.summary();
    }
}
