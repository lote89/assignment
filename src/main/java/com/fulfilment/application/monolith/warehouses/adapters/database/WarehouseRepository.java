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
    public Optional<DomainWarehouse> findById(String id) {
        DbWarehouse db = DbWarehouse.findById(id);
        return Optional.ofNullable(db).map(DwMapper::toDomain);
    }

    @Override
    public DomainWarehouse findByBusinessUnitCode(String businessUnitCode) {
        DbWarehouse db = DbWarehouse.find("businessUnitCode", businessUnitCode).firstResult();
        return DwMapper.toDomain(db);
    }

    @Override
    public List<DomainWarehouse> findAllActive() {
        List<DbWarehouse> dbs = DbWarehouse.list("archivedAt is null");
        return dbs.stream().map(DwMapper::toDomain).collect(Collectors.toList());
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
    public void archive(String id) {
        DbWarehouse db = DbWarehouse.findById(id);
        if (db != null) {
            db.archivedAt = java.time.ZonedDateTime.now();
            db.persist();
        }
    }

    @Override
    @Transactional
    public void delete(String id) {
        DbWarehouse.deleteById(id);
    }
}
