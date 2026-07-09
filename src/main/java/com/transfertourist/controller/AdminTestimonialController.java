package com.transfertourist.controller;

import com.transfertourist.dto.response.TestimonialResponse;
import com.transfertourist.service.TestimonialService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Admin testimonial list (includes unpublished), newest first. {@code ROLE_ADMIN} only. */
@RestController
@RequestMapping("/api/v1/admin/testimonials")
public class AdminTestimonialController {

    private final TestimonialService testimonialService;

    public AdminTestimonialController(TestimonialService testimonialService) {
        this.testimonialService = testimonialService;
    }

    @GetMapping
    public List<TestimonialResponse> list() {
        return testimonialService.adminList();
    }
}
