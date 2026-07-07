-- V1 baseline (Milestone 2.1 foundation).
-- The full domain schema (location, vehicle, transfer_price, booking, ...)
-- arrives in Milestone 2.2. This baseline only creates the settings table so
-- pricing constants are data-driven from day one (CR-2).

CREATE TABLE app_setting (
    key         VARCHAR(64) PRIMARY KEY,
    value       VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Infant-seat unit price in EUR, exposed later via GET /api/v1/settings/pricing
-- so the SPA sidebar total and the server-recomputed total stay in sync (CR-2).
INSERT INTO app_setting (key, value, description)
VALUES ('infant_seat_price', '10', 'Fixed price per infant seat, in EUR (charged per leg).');
