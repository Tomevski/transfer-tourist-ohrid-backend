package com.transfertourist.controller;

import com.transfertourist.dto.common.PageResponse;
import com.transfertourist.dto.response.TestimonialResponse;
import com.transfertourist.service.TestimonialService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Public read endpoint for testimonials. Admin CRUD arrives in Milestone 2.5. */
@RestController
@RequestMapping("/api/v1/testimonials")
public class TestimonialController {

    private final TestimonialService testimonialService;

    public TestimonialController(TestimonialService testimonialService) {
        this.testimonialService = testimonialService;
    }

    @GetMapping
    public PageResponse<TestimonialResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return testimonialService.list(q, rating, page, pageSize);
    }
}
