package com.transfertourist.entity;

import com.transfertourist.constants.TransferPurpose;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;

/**
 * A single directional leg (outbound or return) of a booking. Embedded twice in
 * {@link Booking}, which supplies every column mapping (name/nullable/length) via
 * {@code @AttributeOverride} — the outbound leg's columns are NOT NULL, the
 * return leg's are nullable. This embeddable therefore declares no {@code @Column}
 * metadata of its own (it owns no table); only field-type annotations such as
 * {@link Enumerated} live here. Location ids are plain strings (mirroring the DTO
 * shape); times are {@code HH:mm} strings kept timezone-agnostic, matching the
 * frontend so comparisons stay identical.
 */
@Embeddable
public class Leg {

    private String fromLocationId;

    private String toLocationId;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private TransferPurpose purpose;

    private String flightNumber;

    private String flightTime;

    private String pickupTime;

    public Leg() {
        // JPA + service construction
    }

    public String getFromLocationId() {
        return fromLocationId;
    }

    public void setFromLocationId(String fromLocationId) {
        this.fromLocationId = fromLocationId;
    }

    public String getToLocationId() {
        return toLocationId;
    }

    public void setToLocationId(String toLocationId) {
        this.toLocationId = toLocationId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TransferPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(TransferPurpose purpose) {
        this.purpose = purpose;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getFlightTime() {
        return flightTime;
    }

    public void setFlightTime(String flightTime) {
        this.flightTime = flightTime;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }
}
