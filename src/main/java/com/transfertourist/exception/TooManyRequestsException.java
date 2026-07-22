package com.transfertourist.exception;

/**
 * Thrown when a client exceeds an allowed request rate (e.g. repeated failed
 * logins). Surfaced by {@code GlobalExceptionHandler} as HTTP 429.
 */
public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String message) {
        super(message);
    }
}
