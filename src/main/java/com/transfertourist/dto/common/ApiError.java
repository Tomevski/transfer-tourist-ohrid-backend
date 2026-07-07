package com.transfertourist.dto.common;

import java.time.Instant;
import java.util.Map;

/**
 * Consistent error payload returned by the global exception handler. Shape
 * matches the frontend apiClient: {@code { timestamp, status, code, message, fieldErrors }}
 * where {@code fieldErrors} is a {field -> message} map.
 *
 * @param timestamp   when the error occurred (server time, UTC)
 * @param status      HTTP status code
 * @param code        stable machine-readable error code (e.g. NOT_FOUND, VALIDATION_ERROR)
 * @param message     human-readable summary
 * @param fieldErrors per-field validation messages (empty unless a validation error)
 */
public record ApiError(
        Instant timestamp,
        int status,
        String code,
        String message,
        Map<String, String> fieldErrors
) {

    public static ApiError of(int status, String code, String message) {
        return new ApiError(Instant.now(), status, code, message, Map.of());
    }

    public static ApiError of(int status, String code, String message, Map<String, String> fieldErrors) {
        return new ApiError(Instant.now(), status, code, message, fieldErrors);
    }
}
