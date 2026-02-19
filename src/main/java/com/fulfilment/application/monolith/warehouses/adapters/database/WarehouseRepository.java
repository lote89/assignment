package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

    @Override
    public void create(DomainWarehouse warehouse) {  // VOID return
        DbWarehouse db = DbWarehouse.fromDomain(warehouse);
        db.persist();
    }

    @Override
    public Optional<DomainWarehouse> findById(Long id) {
        return findByIdOptional(id).map(DbWarehouse::toDomain);
    }

    @Override
    public List<DomainWarehouse> findAllActive() {
        return stream("archivedAt is null")
            .map(DbWarehouse::toDomain)
            .collect(Collectors.toList());
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
