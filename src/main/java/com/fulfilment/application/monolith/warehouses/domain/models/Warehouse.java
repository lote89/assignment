package com.fulfilment.application.monolith.warehouses.domain.models;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "warehouse")
public class Warehouse extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public Long id;

    @Column(unique = true, nullable = false)
    public String businessUnitCode;

    @Column(nullable = false)
    public String locationId; // was "location"

    @Column(nullable = false)
    public Integer capacity;

    @Column(nullable = false)
    public Integer stock;

    @Column(nullable = false)
    public ZonedDateTime creationAt;

    public ZonedDateTime archivedAt; // null = active

    public Warehouse() {} // default constructor

    // Constructor with all required fields
    public Warehouse(Long id, String businessUnitCode, String locationId,
                     Integer capacity, Integer stock, ZonedDateTime creationAt) {
        this.id = id;
        this.businessUnitCode = businessUnitCode;
        this.locationId = locationId;
        this.capacity = capacity;
        this.stock = stock;
        this.creationAt = creationAt;
    }

    public boolean isActive() {
        return archivedAt == null;
    }

    // Archive this warehouse
    public void archive() {
        this.archivedAt = ZonedDateTime.now();
    }

    // Update fields from another warehouse instance
    public void updateFrom(Warehouse other) {
        this.businessUnitCode = other.businessUnitCode;
        this.locationId = other.locationId;
        this.capacity = other.capacity;
        this.stock = other.stock;
    }

    // Optional getters if needed
    public Long getId() { return id; }
    public String getBusinessUnitCode() { return businessUnitCode; }
    public String getLocationId() { return locationId; }
    public Integer getCapacity() { return capacity; }
    public Integer getStock() { return stock; }
    public ZonedDateTime getCreationAt() { return creationAt; }
    public ZonedDateTime getArchivedAt() { return archivedAt; }
}
