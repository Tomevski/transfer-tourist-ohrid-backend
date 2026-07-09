package com.transfertourist.dto.common;

/**
 * Minimal {@code { id }} envelope returned by admin delete endpoints, mirroring
 * the frontend services which expect {@code { id }} back from a successful DELETE.
 */
public record IdResponse(String id) {
}
