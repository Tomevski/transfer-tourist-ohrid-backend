package com.transfertourist.controller;

import com.transfertourist.dto.common.PageResponse;
import com.transfertourist.dto.request.BookingCreateRequest;
import com.transfertourist.dto.response.BookingResponse;
import com.transfertourist.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Bookings: the public create path plus the admin list and confirm/decline
 * actions. Only {@code POST /bookings} is public; the list and status changes
 * are {@code ROLE_ADMIN} per the security matrix.
 */
@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@Valid @RequestBody BookingCreateRequest request) {
        return bookingService.create(request);
    }

    @GetMapping
    public PageResponse<BookingResponse> adminList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return bookingService.adminList(status, q, page, pageSize);
    }

    @PatchMapping("/{id}/confirm")
    public BookingResponse confirm(@PathVariable String id) {
        return bookingService.confirm(id);
    }

    @PatchMapping("/{id}/decline")
    public BookingResponse decline(@PathVariable String id) {
        return bookingService.decline(id);
    }
}
