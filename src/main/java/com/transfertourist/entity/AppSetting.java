package com.transfertourist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * A key/value configuration row so pricing constants stay data-driven (CR-2).
 * Seeded with {@code infant_seat_price=10} in {@code V1__baseline.sql} and
 * exposed later via {@code GET /api/v1/settings/pricing}.
 */
@Entity
@Table(name = "app_setting")
public class AppSetting {

    // `key` and `value` are reserved words (H2/SQL); quote them so schema
    // generation and validation work across H2 (tests) and Postgres.
    @Id
    @Column(name = "\"key\"", length = 64)
    private String key;

    @Column(name = "\"value\"", nullable = false)
    private String value;

    @Column
    private String description;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AppSetting() {
        // JPA
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
