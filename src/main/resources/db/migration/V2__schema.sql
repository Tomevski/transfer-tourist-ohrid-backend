-- V2 domain schema (Milestone 2.2).
-- Column names/types are aligned with the JPA entities so Hibernate
-- `ddl-auto: validate` passes against this Flyway-owned schema. Assigned string
-- ids mirror the frontend fixtures, keeping the mock -> API swap zero-touch.

CREATE TABLE location (
    id          VARCHAR(64) PRIMARY KEY,
    name        VARCHAR(160) NOT NULL,
    slug        VARCHAR(160) NOT NULL UNIQUE,
    category    VARCHAR(16) NOT NULL,
    city        VARCHAR(120),
    country     VARCHAR(120) NOT NULL,
    lat         DOUBLE PRECISION,
    lng         DOUBLE PRECISION,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order  INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX idx_location_category ON location (category);

CREATE TABLE vehicle (
    id          VARCHAR(64) PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    capacity    INTEGER NOT NULL,
    description VARCHAR(500) NOT NULL,
    image_url   VARCHAR(255) NOT NULL,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order  INTEGER NOT NULL DEFAULT 0
);

-- Ordered @ElementCollection of vehicle feature labels.
CREATE TABLE vehicle_feature (
    vehicle_id  VARCHAR(64) NOT NULL REFERENCES vehicle (id) ON DELETE CASCADE,
    position    INTEGER NOT NULL,
    label       VARCHAR(120) NOT NULL,
    PRIMARY KEY (vehicle_id, position)
);

CREATE TABLE transfer_price (
    id                VARCHAR(64) PRIMARY KEY,
    from_location_id  VARCHAR(64) NOT NULL REFERENCES location (id),
    to_location_id    VARCHAR(64) NOT NULL REFERENCES location (id),
    vehicle_id        VARCHAR(64) NOT NULL REFERENCES vehicle (id),
    price             NUMERIC(10, 2) NOT NULL,
    CONSTRAINT uq_transfer_price_route_vehicle UNIQUE (from_location_id, to_location_id, vehicle_id)
);
CREATE INDEX idx_transfer_price_route ON transfer_price (from_location_id, to_location_id);

CREATE TABLE testimonial (
    id          VARCHAR(64) PRIMARY KEY,
    author_name VARCHAR(120) NOT NULL,
    location    VARCHAR(120),
    country     VARCHAR(120),
    rating      INTEGER NOT NULL,
    content     VARCHAR(2000) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL,
    published   BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE contact_message (
    id          VARCHAR(64) PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    surname     VARCHAR(120) NOT NULL,
    email       VARCHAR(160) NOT NULL,
    message     VARCHAR(2000) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL,
    handled     BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE app_user (
    id            VARCHAR(64) PRIMARY KEY,
    email         VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(32) NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL
);

CREATE TABLE booking_request (
    id             VARCHAR(64) PRIMARY KEY,
    reference_code VARCHAR(32) NOT NULL UNIQUE,
    status         VARCHAR(16) NOT NULL,
    trip_type      VARCHAR(16) NOT NULL,
    passengers     INTEGER NOT NULL,
    infant_seats   INTEGER NOT NULL DEFAULT 0,
    comments       VARCHAR(1000),
    vehicle_id     VARCHAR(64) NOT NULL REFERENCES vehicle (id),
    total_price    NUMERIC(10, 2) NOT NULL,

    -- Customer (embedded)
    customer_first_name VARCHAR(120) NOT NULL,
    customer_last_name  VARCHAR(120) NOT NULL,
    customer_email      VARCHAR(160) NOT NULL,
    customer_phone      VARCHAR(40) NOT NULL,

    -- Outbound leg (embedded, always present)
    outbound_from_location_id VARCHAR(64) NOT NULL,
    outbound_to_location_id   VARCHAR(64) NOT NULL,
    outbound_date             DATE NOT NULL,
    outbound_purpose          VARCHAR(16) NOT NULL,
    outbound_flight_number    VARCHAR(20),
    outbound_flight_time      VARCHAR(5),
    outbound_pickup_time      VARCHAR(5),

    -- Return leg (embedded, present only for RETURN trips)
    return_from_location_id VARCHAR(64),
    return_to_location_id   VARCHAR(64),
    return_date             DATE,
    return_purpose          VARCHAR(16),
    return_flight_number    VARCHAR(20),
    return_flight_time      VARCHAR(5),
    return_pickup_time      VARCHAR(5),

    created_at   TIMESTAMPTZ NOT NULL,
    updated_at   TIMESTAMPTZ,
    confirmed_at TIMESTAMPTZ
);
CREATE INDEX idx_booking_status ON booking_request (status);
CREATE INDEX idx_booking_created_at ON booking_request (created_at);
CREATE INDEX idx_booking_route ON booking_request (outbound_from_location_id, outbound_to_location_id);
