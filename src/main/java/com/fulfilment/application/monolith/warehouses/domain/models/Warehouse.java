package com.fulfilment.application.monolith.warehouses.domain.models;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "warehouse")
public class Warehouse extends PanacheEntityBase {

    @Id @GeneratedValue
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

    private ZonedDateTime archivedAt;

    // Constructor
    public Warehouse() {}
    
    public Warehouse(Long id, String businessUnitCode, String location, Integer capacity, Integer stock, ZonedDateTime creationAt) {
        this.id = id;
        this.businessUnitCode = businessUnitCode;
        this.location = location;
        this.capacity = capacity;
        this.stock = stock;
        this.creationAt = creationAt;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBusinessUnitCode() { return businessUnitCode; }
    public void setBusinessUnitCode(String businessUnitCode) { this.businessUnitCode = businessUnitCode; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public ZonedDateTime getCreationAt() { return creationAt; }
    public void setCreationAt(ZonedDateTime creationAt) { this.creationAt = creationAt; }

    public ZonedDateTime getArchivedAt() { return archivedAt; }
    public void setArchivedAt(ZonedDateTime archivedAt) { this.archivedAt = archivedAt; }

    public boolean isActive() { return archivedAt == null; }

    public void archive() { this.archivedAt = ZonedDateTime.now(); }

    public void updateFrom(Warehouse other) {
        this.businessUnitCode = other.businessUnitCode;
        this.location = other.location;
        this.capacity = other.capacity;
        this.stock = other.stock;
    }
}
