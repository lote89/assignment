package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

    @Override
    public DomainWarehouse create(DomainWarehouse warehouse) {
        DbWarehouse db = DbWarehouse.fromDomain(warehouse);
        db.persist();
        return DbWarehouse.toDomain(db);
    }

    @Override
    public Optional<DomainWarehouse> findById(Long id) {
        return findByIdOptional(id).map(DbWarehouse::toDomain);
    }

    @Override
    public void update(DomainWarehouse warehouse) {
        DbWarehouse db = findByIdOptional(warehouse.getId())
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        DbWarehouse.fromDomain(warehouse).persist();
    }

    @Override
    public void archive(Long id) {
        DbWarehouse db = findByIdOptional(id)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        db.archivedAt = java.time.ZonedDateTime.now();
        db.persist();
    }

    @Override
    public void delete(Long id) {
        deleteById(id);
    }
}
