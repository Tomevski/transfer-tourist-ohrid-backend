package com.transfertourist.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Admin create/update payload for a transfer price (mirrors the frontend
 * {@code TransferPriceInput = Omit<TransferPrice, 'id'>}). {@code from != to} and
 * route-uniqueness are enforced in the service.
 */
public record TransferPriceRequest(
        @NotBlank(message = "From is required") String fromLocationId,
        @NotBlank(message = "To is required") String toLocationId,
        @NotBlank(message = "Vehicle is required") String vehicleId,
        @NotNull(message = "Price is required") @Positive(message = "Price must be greater than zero") BigDecimal price
) {
}
