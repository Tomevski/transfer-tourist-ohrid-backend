package com.transfertourist.entity;

import com.transfertourist.constants.BookingStatus;
import com.transfertourist.constants.TripType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A transfer request submitted from the booking form. Carries the outbound leg
 * (always) and an optional return leg, the chosen vehicle, passenger/infant
 * counts and the server-recomputed {@code totalPrice} (CR-2). Persisted as
 * {@code PENDING}; the operator confirms or declines it.
 */
@Entity
@Table(name = "booking_request")
public class Booking {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "reference_code", nullable = false, unique = true, length = 32)
    private String referenceCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private BookingStatus status = BookingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_type", nullable = false, length = 16)
    private TripType tripType;

    @Column(nullable = false)
    private int passengers;

    @Column(name = "infant_seats", nullable = false)
    private int infantSeats;

    @Column(length = 1000)
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Embedded
    private Customer customer;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fromLocationId", column = @Column(name = "outbound_from_location_id", nullable = false, length = 64)),
            @AttributeOverride(name = "toLocationId", column = @Column(name = "outbound_to_location_id", nullable = false, length = 64)),
            @AttributeOverride(name = "date", column = @Column(name = "outbound_date", nullable = false)),
            @AttributeOverride(name = "purpose", column = @Column(name = "outbound_purpose", nullable = false, length = 16)),
            @AttributeOverride(name = "flightNumber", column = @Column(name = "outbound_flight_number", length = 20)),
            @AttributeOverride(name = "flightTime", column = @Column(name = "outbound_flight_time", length = 5)),
            @AttributeOverride(name = "pickupTime", column = @Column(name = "outbound_pickup_time", length = 5))
    })
    private Leg outbound;

    /** Present only for {@link TripType#RETURN}; all columns are nullable. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fromLocationId", column = @Column(name = "return_from_location_id", length = 64)),
            @AttributeOverride(name = "toLocationId", column = @Column(name = "return_to_location_id", length = 64)),
            @AttributeOverride(name = "date", column = @Column(name = "return_date")),
            @AttributeOverride(name = "purpose", column = @Column(name = "return_purpose", length = 16)),
            @AttributeOverride(name = "flightNumber", column = @Column(name = "return_flight_number", length = 20)),
            @AttributeOverride(name = "flightTime", column = @Column(name = "return_flight_time", length = 5)),
            @AttributeOverride(name = "pickupTime", column = @Column(name = "return_pickup_time", length = 5))
    })
    private Leg returnLeg;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    protected Booking() {
        // JPA
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public TripType getTripType() {
        return tripType;
    }

    public void setTripType(TripType tripType) {
        this.tripType = tripType;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public int getInfantSeats() {
        return infantSeats;
    }

    public void setInfantSeats(int infantSeats) {
        this.infantSeats = infantSeats;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Leg getOutbound() {
        return outbound;
    }

    public void setOutbound(Leg outbound) {
        this.outbound = outbound;
    }

    public Leg getReturnLeg() {
        return returnLeg;
    }

    public void setReturnLeg(Leg returnLeg) {
        this.returnLeg = returnLeg;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
}
