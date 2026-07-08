package com.transfertourist.dto.response;

import java.math.BigDecimal;

/**
 * Pricing constants the SPA needs so its displayed total matches the
 * server-recomputed one (CR-2). Currently just the per-seat infant price.
 */
public record PricingSettingsResponse(BigDecimal infantSeatPrice) {
}
