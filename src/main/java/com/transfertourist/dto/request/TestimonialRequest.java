package com.transfertourist.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Admin create/update payload for a testimonial (mirrors the frontend
 * {@code TestimonialInput}). {@code createdAt} is server-assigned.
 */
public record TestimonialRequest(
        @NotBlank(message = "Author name is required") @Size(max = 120) String authorName,
        @Size(max = 120) String location,
        @Size(max = 120) String country,
        @Min(value = 1, message = "Rating must be 1–5") @Max(value = 5, message = "Rating must be 1–5") int rating,
        @NotBlank(message = "Content is required") @Size(max = 2000) String content,
        boolean published
) {
}
