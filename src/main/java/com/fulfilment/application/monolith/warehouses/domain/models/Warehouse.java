package com.fulfilment.application.monolith.warehouses.domain.models;

import java.time.ZonedDateTime;

public class Warehouse {
    private Long id;
    private String code;
    private String name;
    private Integer streetNumber;
    private Integer postalCode;
    private String city;
    private ZonedDateTime creationAt;
    private ZonedDateTime archivedAt;

    // Default constructor
    public Warehouse() {}

    // Constructor for DomainWarehouse mapping
    public Warehouse(Long id, String code, String name, Integer streetNumber, 
                    Integer postalCode, String city, ZonedDateTime creationAt, 
                    ZonedDateTime archivedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.streetNumber = streetNumber;
        this.postalCode = postalCode;
        this.city = city;
        this.creationAt = creationAt;
        this.archivedAt = archivedAt;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getStreetNumber() { return streetNumber; }
    public void setStreetNumber(Integer streetNumber) { this.streetNumber = streetNumber; }
    
    public Integer getPostalCode() { return postalCode; }
    public void setPostalCode(Integer postalCode) { this.postalCode = postalCode; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public ZonedDateTime getCreationAt() { return creationAt; }
    public void setCreationAt(ZonedDateTime creationAt) { this.creationAt = creationAt; }
    
    public ZonedDateTime getArchivedAt() { return archivedAt; }
    public void setArchivedAt(ZonedDateTime archivedAt) { this.archivedAt = archivedAt; }
}
