package com.fulfilment.application.monolith.warehouses.adapters.database;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "warehouse")
@Cacheable
public class DbWarehouse extends PanacheEntity {  // ADD THIS EXTENDS

    public String businessUnitCode;
    public String locationId;  // CHANGE: was "location"
    public Integer capacity;
    public Integer stock;
    public ZonedDateTime createdAt;  // FIX: was "creationAt"
    public ZonedDateTime archivedAt;

    public DbWarehouse() {}
}
