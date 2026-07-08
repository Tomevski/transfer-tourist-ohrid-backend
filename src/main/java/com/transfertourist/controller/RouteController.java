package com.transfertourist.controller;

import com.transfertourist.dto.response.RouteEstimateResponse;
import com.transfertourist.service.RouteEstimateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Public route distance/duration estimate. Admin route CRUD arrives in Milestone 2.5. */
@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {

    private final RouteEstimateService routeEstimateService;

    public RouteController(RouteEstimateService routeEstimateService) {
        this.routeEstimateService = routeEstimateService;
    }

    @GetMapping("/estimate")
    public RouteEstimateResponse estimate(@RequestParam("from") String from,
                                          @RequestParam("to") String to) {
        return routeEstimateService.estimate(from, to);
    }
}
