package com.transfertourist.dto.response;

import java.time.Instant;

/** Public representation of a {@code testimonial}. Mirrors the frontend {@code Testimonial}. */
public record TestimonialResponse(
        String id,
        String authorName,
        String location,
        String country,
        int rating,
        String content,
        Instant createdAt,
        boolean published
) {
}
