package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore {

    @Override
    @Transactional
    public void create(DomainWarehouse warehouse) {
        DbWarehouse.fromDomain(warehouse).persist();
    }

    @Override
    public Optional<DomainWarehouse> findById(Long id) {
        return Optional.ofNullable(DbWarehouse.findById(id))
                      .map(DbWarehouse::toDomain);
    }

    @Override
    public DomainWarehouse findByBusinessUnitCode(String businessUnitCode) {
        DbWarehouse db = DbWarehouse.find("businessUnitCode = ?1", businessUnitCode)
                                   .firstResult();
        return db != null ? DbWarehouse.toDomain(db) : null;
    }

    @Override
    public List<DomainWarehouse> findAllActive() {
        return DbWarehouse.stream("archivedAt is null")
                         .list()
                         .stream()
                         .map(DbWarehouse::toDomain)
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
        DbWarehouse db = DbWarehouse.findById(warehouse.getId());
        if (db != null) {
            DbWarehouse.fromDomain(warehouse).persist();
        }
    }

    @Override
    @Transactional
    public void archive(Long id) {
        DbWarehouse db = DbWarehouse.findById(id);
        if (db != null) {
            db.archivedAt = java.time.ZonedDateTime.now();
            db.persist();
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        DbWarehouse.deleteById(id);
    }
}

