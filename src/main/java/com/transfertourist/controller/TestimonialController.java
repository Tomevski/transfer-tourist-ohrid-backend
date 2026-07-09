package com.transfertourist.controller;

import com.transfertourist.dto.common.IdResponse;
import com.transfertourist.dto.common.PageResponse;
import com.transfertourist.dto.request.TestimonialRequest;
import com.transfertourist.dto.response.TestimonialResponse;
import com.transfertourist.service.TestimonialService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Testimonials: public paged reads plus admin mutations. The admin list (incl.
 * unpublished) is served from {@code /admin/testimonials}
 * (see {@link AdminTestimonialController}).
 */
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TestimonialResponse create(@Valid @RequestBody TestimonialRequest request) {
        return testimonialService.create(request);
    }

    @PutMapping("/{id}")
    public TestimonialResponse update(@PathVariable String id, @Valid @RequestBody TestimonialRequest request) {
        return testimonialService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public IdResponse delete(@PathVariable String id) {
        testimonialService.delete(id);
        return new IdResponse(id);
    }

    @PatchMapping("/{id}/publish")
    public TestimonialResponse togglePublish(@PathVariable String id) {
        return testimonialService.togglePublish(id);
    }
}
