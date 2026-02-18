package com.fulfilment.application.monolith.warehouses.domain.models;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "warehouse")
public class Warehouse extends PanacheEntityBase {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String businessUnitCode;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private ZonedDateTime creationAt;

    private ZonedDateTime archivedAt; // Null = active

    // --- Constructors ---
    public Warehouse() {}

    public Warehouse(Long id, String businessUnitCode, String location, Integer capacity, Integer stock, ZonedDateTime creationAt) {
        this.id = id;
        this.businessUnitCode = businessUnitCode;
        this.location = location;
        this.capacity = capacity;
        this.stock = stock;
        this.creationAt = creationAt;
    }

    // --- Getters ---
    public Long id() { return id; }
    public String businessUnitCode() { return businessUnitCode; }
    public String locationId() { return location; }
    public Integer capacity() { return capacity; }
    public Integer stock() { return stock; }
    public ZonedDateTime creationAt() { return creationAt; }
    public ZonedDateTime archivedAt() { return archivedAt; }

    // --- Business Methods ---
    public boolean isActive() {
        return archivedAt == null;
    }

    public void archive() {
        this.archivedAt = ZonedDateTime.now();
    }

    public void updateFrom(Warehouse other) {
        this.businessUnitCode = other.businessUnitCode;
        this.location = other.location;
        this.capacity = other.capacity;
        this.stock = other.stock;
    }
}
