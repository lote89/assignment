package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore {

    @Override
    @Transactional
    public void create(DomainWarehouse warehouse) {
        DbWarehouse.fromDomain(warehouse).persist();
    }

    @Override
    public Optional<DomainWarehouse> findById(String id) {  // String ID
        DbWarehouse db = DbWarehouse.findById(id);
        return db != null ? Optional.of(DbWarehouse.toDomain(db)) : Optional.empty();
    }

    @Override
    public DomainWarehouse findByBusinessUnitCode(String businessUnitCode) {
        DbWarehouse db = DbWarehouse.find("businessUnitCode = ?1", businessUnitCode).firstResult();
        return db != null ? DbWarehouse.toDomain(db) : null;
    }

    @Override
    public List<DomainWarehouse> findAllActive() {
        return DbWarehouse.stream("archivedAt is null")
            .list()
            .stream()
            .map(db -> DbWarehouse.toDomain(db))  // Lambda, not method ref
            .collect(Collectors.toList());
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
        DbWarehouse.fromDomain(warehouse).persist();
    }

    @Override
    @Transactional
    public void archive(String id) {  // String ID
        DbWarehouse db = DbWarehouse.findById(id);
        if (db != null) {
            db.archivedAt = java.time.ZonedDateTime.now();
            db.persist();
        }
    }

    @Override
    @Transactional
    public void delete(String id) {  // String ID
        DbWarehouse.deleteById(id);
    }
}
