package com.transfertourist.exception;

/**
 * Thrown when a request is well-formed but violates a business rule — e.g. a
 * duplicate transfer-price pair or deleting an entity that is still referenced.
 * Mapped to HTTP 409 by {@link GlobalExceptionHandler} so the SPA can surface
 * the message directly.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
