package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore {

    @Override
    @Transactional
    public void create(DomainWarehouse warehouse) {
        DbWarehouse db = DbWarehouse.fromDomain(warehouse);
        db.persist();
    }

    @Override
    public Optional<DomainWarehouse> findById(Long id) {
        Optional<DbWarehouse> db = DbWarehouse.findByIdOptional(id);
        return db.map(DwMapper::toDomain);
    }

    @Override
    public List<DomainWarehouse> findAllActive() {
        return DbWarehouse.stream("archivedAt is null").map(DwMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countActiveByLocation(String location) {
        return DbWarehouse.count("location = ?1 and archivedAt is null", location);
    }

    @Override
    public boolean existsByBusinessUnitCode(String businessUnitCode) {
        return DbWarehouse.count("businessUnitCode = ?1", businessUnitCode) > 0;
    }

    @Override
    @Transactional
    public void update(DomainWarehouse warehouse) {
        DbWarehouse db = DbWarehouse.findByIdOptional(warehouse.getId())
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        DbWarehouse.fromDomain(warehouse).persist();
    }

    @Override
    @Transactional
    public void archive(Long id) {
        DbWarehouse db = DbWarehouse.findByIdOptional(id)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        db.archivedAt = java.time.ZonedDateTime.now();
        db.persist();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        DbWarehouse.deleteById(id);
    }
}
