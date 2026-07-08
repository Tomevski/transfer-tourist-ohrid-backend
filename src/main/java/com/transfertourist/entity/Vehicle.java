package com.transfertourist.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * A bookable vehicle class. Vehicles carry <strong>no price</strong> — pricing
 * lives on {@link TransferPrice} per route + vehicle (the final pricing model).
 */
@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false, length = 500)
    private String description;

    @ElementCollection
    @CollectionTable(name = "vehicle_feature", joinColumns = @JoinColumn(name = "vehicle_id"))
    @OrderColumn(name = "position")
    @Column(name = "label", nullable = false, length = 120)
    private List<String> features = new ArrayList<>();

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected Vehicle() {
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
