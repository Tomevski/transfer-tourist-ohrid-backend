package com.transfertourist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

/**
 * Admin-managed price for a {@code From + To + Vehicle} combination — the final
 * pricing model (CR-1). The same vehicle can cost different amounts on different
 * routes, and reverse routes are seeded so return legs price each direction.
 * Unique per {@code (from, to, vehicle)}.
 */
@Entity
@Table(name = "transfer_price", uniqueConstraints = @UniqueConstraint(
        name = "uq_transfer_price_route_vehicle",
        columnNames = {"from_location_id", "to_location_id", "vehicle_id"}))
public class TransferPrice {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_location_id", nullable = false)
    private Location fromLocation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_location_id", nullable = false)
    private Location toLocation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    /** Price for this route + vehicle combination, in EUR. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    protected TransferPrice() {
        // JPA
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(Location fromLocation) {
        this.fromLocation = fromLocation;
    }

    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
