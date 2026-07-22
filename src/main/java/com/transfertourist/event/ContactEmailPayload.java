package com.transfertourist.event;

/**
 * Fully-resolved, immutable snapshot of a Contact Us submission for email
 * rendering. Mirrors {@link BookingEmailPayload}: assembled inside the
 * {@code @Transactional} service method so the async, post-commit email
 * listener never touches the persistence layer.
 */
public record ContactEmailPayload(
        String fullName,
        String firstName,
        String email,
        String message,
        /* Human-readable submission time, pre-formatted so templates render no temporals. */
        String receivedAt
) {
}
