package com.transfertourist.entity;

import com.transfertourist.constants.LocationCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A place a transfer can start from or end at (airport, city, hotel or resort).
 * Reference data managed by the admin (CR-1). The {@code id} is an assigned
 * string matching the frontend fixtures (e.g. {@code loc-ohd}).
 */
@Entity
@Table(name = "location")
public class Location {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, unique = true, length = 160)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private LocationCategory category;

    @Column(length = 120)
    private String city;

    @Column(nullable = false, length = 120)
    private String country;

    private Double lat;

    private Double lng;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected Location() {
        // JPA
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public LocationCategory getCategory() {
        return category;
    }

    public void setCategory(LocationCategory category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
