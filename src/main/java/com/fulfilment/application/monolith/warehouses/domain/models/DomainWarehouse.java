package com.fulfilment.application.monolith.warehouses.domain.models;

import java.time.ZonedDateTime;

public class DomainWarehouse {
    private String id;  
    private String businessUnitCode;
    private String location;
    private Integer capacity;
    private Integer stock;
    private ZonedDateTime creationAt;
    private ZonedDateTime archivedAt;

    public String getBusinessUnitCode() { return businessUnitCode; }
    public void setBusinessUnitCode(String businessUnitCode) { this.businessUnitCode = businessUnitCode; }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public void setId(Long id) { this.id = id != null ? id.toString() : null; }  // Overload
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public void setCreationAt(ZonedDateTime creationAt) {
    this.creationAt = creationAt;
}
   public DomainWarehouse(Long id, String businessUnitCode, String location, Integer capacity, 
                      Integer stock, ZonedDateTime creationAt, ZonedDateTime archivedAt) {
    this.id = id;
    this.businessUnitCode = businessUnitCode;
    this.location = location;
    this.capacity = capacity;
    this.stock = stock;
    this.creationAt = creationAt;
    this.archivedAt = archivedAt;
}

public ZonedDateTime getCreationAt() {
    return creationAt;
}

public ZonedDateTime getArchivedAt() {
    return archivedAt;
}

public void archive() {
    this.archivedAt = ZonedDateTime.now();
}
}
