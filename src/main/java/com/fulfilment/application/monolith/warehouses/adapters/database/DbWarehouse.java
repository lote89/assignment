package com.fulfilment.application.monolith.warehouses.adapters.database;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.ZonedDateTime;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;  

@Entity
@Table(name = "warehouse")
@Cacheable
public class DbWarehouse extends PanacheEntity {

    @Column(unique = true, nullable = false)
    public String businessUnitCode;
    
    @Column(nullable = false)
    public String location;  

    @Column(nullable = false)
    public Integer capacity;
    @Column(nullable = false)
    public Integer stock;
    
    @Column(nullable = false)
    public ZonedDateTime creationAt;  

    public ZonedDateTime archivedAt;

    public DbWarehouse() {}

    public static DbWarehouse fromDomain(Warehouse source) {
        DbWarehouse db = new DbWarehouse();
        db.businessUnitCode = source.getBusinessUnitCode();
        db.location = source.getLocation();
        db.capacity = source.getCapacity();
        db.stock = source.getStock();
        db.creationAt = source.getCreationAt();
        db.archivedAt = source.getArchivedAt();
        return db;
    }

    
  public Warehouse toDomain() {
    return new Warehouse(
        id,  
        businessUnitCode,
        location,
        capacity,
        stock,
        creationAt,
        archivedAt
    );
}
}
