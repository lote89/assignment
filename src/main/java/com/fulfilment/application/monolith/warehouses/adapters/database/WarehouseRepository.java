package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore {

    @Override
    @Transactional
    public void create(Warehouse warehouse) {
        if (existsByBusinessUnitCode(warehouse.businessUnitCode())) {
            throw new IllegalStateException("Business unit code already exists");
        }
        DbWarehouse db = new DbWarehouse();
        mapToDb(warehouse, db);
        db.createdAt = LocalDateTime.now();
        db.persist();
    }

    @Override
    @Transactional
    public void update(Warehouse warehouse) {
        DbWarehouse db = findDbById(warehouse.id())
            .orElseThrow(() -> new IllegalStateException("Warehouse not found"));
        mapToDb(warehouse, db);
        db.persist();
    }

    @Override
    @Transactional
    public void archive(Long id) {
        DbWarehouse db = DbWarehouse.findById(id)
            .orElseThrow(() -> new IllegalStateException("Warehouse not found"));
        if (db.archivedAt == null) {
            db.archivedAt = LocalDateTime.now();
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

    public long countActiveByLocation(String locationId) {
        return DbWarehouse.count("locationId = ?1 and archivedAt is null", locationId);
    }

    public boolean existsByBusinessUnitCode(String buCode) {
        return DbWarehouse.count("businessUnitCode = ?1 and archivedAt is null", buCode) > 0;
    }

    private Optional<DbWarehouse> findDbById(Long id) {
        return DbWarehouse.find("id = ?1 and archivedAt is null", id).firstResultOptional();
    }

    private void mapToDb(Warehouse source, DbWarehouse target) {
        target.businessUnitCode = source.businessUnitCode();
        target.locationId = source.locationId();
        target.capacity = source.capacity();
        target.stock = source.stock();
    }

    private Warehouse mapToDomain(DbWarehouse db) {
        return new Warehouse(
            db.id, 
            db.businessUnitCode, 
            db.locationId, 
            db.capacity, 
            db.stock
        );
    }
}
