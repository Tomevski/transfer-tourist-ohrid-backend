package com.transfertourist.entity;

import com.transfertourist.constants.TransferPurpose;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;

/**
 * A single directional leg (outbound or return) of a booking. Embedded twice in
 * {@link Booking} with column-name overrides. Location ids are stored as plain
 * strings (mirroring the DTO shape); times are {@code HH:mm} strings kept
 * timezone-agnostic, matching the frontend so comparisons stay identical.
 */
@Embeddable
public class Leg {

    @Column(name = "from_location_id", nullable = false, length = 64)
    private String fromLocationId;

    @Column(name = "to_location_id", nullable = false, length = 64)
    private String toLocationId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, length = 16)
    private TransferPurpose purpose;

    @Column(name = "flight_number", length = 20)
    private String flightNumber;

    @Column(name = "flight_time", length = 5)
    private String flightTime;

    @Column(name = "pickup_time", length = 5)
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
