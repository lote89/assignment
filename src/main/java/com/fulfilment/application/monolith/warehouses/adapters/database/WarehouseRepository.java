package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions.WarehouseDomainException;
import java.time.ZonedDateTime;  // FIX: ZonedDateTime not LocalDateTime
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore {

    @Override
    @Transactional
    public void create(Warehouse warehouse) {
        if (existsByBusinessUnitCode(warehouse.getBusinessUnitCode())) {  
            throw new WarehouseDomainException("Warehouse with this businessUnitCode already exists");
        }
        DbWarehouse db = new DbWarehouse();
        mapToDb(warehouse, db);
        db.creationAt = ZonedDateTime.now(); 
        db.persist();
    }

    @Override
    @Transactional
    public void update(Warehouse warehouse) {
        DbWarehouse db = findDbById(warehouse.getId())  
            .orElseThrow(() -> new IllegalStateException("Warehouse not found"));
        mapToDb(warehouse, db);
        db.persist();
    }
    

    @Override
    @Transactional
    public void archive(Long id) {
        DbWarehouse db = findDbById(warehouse.getId()).orElse(null);
        if (db == null) {
            throw new IllegalStateException("Warehouse not found");
         }
        if (db.archivedAt == null) {
            db.archivedAt = ZonedDateTime.now();  
            db.persist();
        }
    }

    @Override
    public Optional<Warehouse> findById(Long id) {
        return findDbById(id).map(this::mapToDomain);
    }

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
        DbWarehouse db = DbWarehouse.find("businessUnitCode = ?1 and archivedAt is null", buCode).firstResult();
        return db != null ? mapToDomain(db) : null;
    }

    @Override
    public List<Warehouse> findAllActive() {
        return DbWarehouse.find("archivedAt is null").list().stream()
            .map(this::mapToDomain)
            .collect(Collectors.toList());
    }

    @Override  // ADD @Override
    public long countActiveByLocation(String location) {  
        return DbWarehouse.count("location = ?1 and archivedAt is null", location);
    }

    @Override  // ADD @Override
    public boolean existsByBusinessUnitCode(String buCode) {
        return DbWarehouse.count("businessUnitCode = ?1 and archivedAt is null", buCode) > 0;
    }

    private Optional<DbWarehouse> findDbById(Long id) {
        return DbWarehouse.find("id = ?1 and archivedAt is null", id).firstResultOptional();
    }

    private void mapToDb(Warehouse source, DbWarehouse target) {
        target.businessUnitCode = source.getBusinessUnitCode();  
        target.location = source.getLocation();                  
        target.capacity = source.getCapacity();                
        target.stock = source.getStock();                        
        target.creationAt = source.getCreationAt();              
        target.archivedAt = source.getArchivedAt();
    }

    private Warehouse mapToDomain(DbWarehouse db) {
        return new Warehouse(
            db.id,         
            db.businessUnitCode, 
            db.location,        
            db.capacity, 
            db.stock,
            db.creationAt,      
            db.archivedAt
        );
    }
}
