package com.fulfilment.application.monolith.warehouses.domain.models;

import java.time.ZonedDateTime;

public class Warehouse {
    private Long id;
    private String businessUnitCode;
    private String location;
    private Integer capacity;
    private Integer stock;
    private ZonedDateTime creationAt;
    private ZonedDateTime archivedAt;

    public Warehouse() {}

    public Warehouse(Long id, String businessUnitCode, String location, Integer capacity, Integer stock, ZonedDateTime creationAt, ZonedDateTime archivedAt) {
        this.id = id;
        this.businessUnitCode = businessUnitCode;
        this.location = location;
        this.capacity = capacity;
        this.stock = stock;
        this.creationAt = creationAt;
        this.archivedAt = archivedAt;
    }

    
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
        this.businessUnitCode = other.getBusinessUnitCode();
        this.location = other.getLocation();
        this.capacity = other.getCapacity();
        this.stock = other.getStock();
    }
}
