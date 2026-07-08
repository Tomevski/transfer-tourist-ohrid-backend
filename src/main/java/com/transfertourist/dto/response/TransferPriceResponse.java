package com.transfertourist.dto.response;

import java.math.BigDecimal;

/**
 * Public representation of a transfer price (From + To + Vehicle -> price).
 * Mirrors the frontend {@code TransferPrice} type: location/vehicle are exposed
 * as ids, not nested objects.
 */
public record TransferPriceResponse(
        String id,
        String fromLocationId,
        String toLocationId,
        String vehicleId,
        BigDecimal price
) {
}
