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


    public boolean isActive() { return archivedAt == null; }
    public void archive() { this.archivedAt = ZonedDateTime.now(); }

    public void updateFrom(Warehouse other) {
        this.businessUnitCode = other.getBusinessUnitCode();  // Use getters
        this.location = other.getLocation();
        this.capacity = other.getCapacity();
        this.stock = other.getStock();
    }
}
