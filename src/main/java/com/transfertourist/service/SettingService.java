package com.transfertourist.service;

import com.transfertourist.dto.response.PricingSettingsResponse;
import com.transfertourist.repository.AppSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Reads application settings. The single source of truth for pricing constants
 * (CR-2): both the {@code /settings/pricing} endpoint and {@code BookingService}
 * resolve the infant-seat price here so the SPA total and the persisted total agree.
 */
@Service
@Transactional(readOnly = true)
public class SettingService {

    private static final String INFANT_SEAT_PRICE_KEY = "infant_seat_price";
    private static final BigDecimal DEFAULT_INFANT_SEAT_PRICE = BigDecimal.TEN;

    private final AppSettingRepository appSettingRepository;

    public SettingService(AppSettingRepository appSettingRepository) {
        this.appSettingRepository = appSettingRepository;
    }

    /** Fixed price per infant seat, in EUR (charged per leg). */
    public BigDecimal infantSeatPrice() {
        return appSettingRepository.findById(INFANT_SEAT_PRICE_KEY)
                .map(setting -> new BigDecimal(setting.getValue()))
                .orElse(DEFAULT_INFANT_SEAT_PRICE);
    }

    public PricingSettingsResponse getPricing() {
        return new PricingSettingsResponse(infantSeatPrice());
    }
}
