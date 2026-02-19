package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions.WarehouseDomainException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore {

    @Override
    @Transactional
    public void create(DomainWarehouse warehouse) {
        if (existsByBusinessUnitCode(warehouse.getBusinessUnitCode())) {
            throw new WarehouseDomainException("Warehouse with this businessUnitCode already exists");
        }
        DbWarehouse db = new DbWarehouse();
        mapToDb(warehouse, db);
        db.creationAt = ZonedDateTime.now();
        db.persist();
    }

     @Override
     public void update(DomainWarehouse warehouse) {
          DbWarehouse db = DbWarehouse.fromDomain(warehouse);
      db.persist();
     }
    
    @Override
    @Transactional
    public void archive(Long id) {
       public Optional<DomainWarehouse> findByCode(String warehouseCode) {
    return find("businessUnitCode", warehouseCode)
        .firstResultOptional()
        .map(DbWarehouse::toDomain);
}
    }

    @Override
    public Optional<DomainWarehouse> findById(Long id) {
        return findDbById(id).map(this::mapToDomain);
    }

    @Override
    public DomainWarehouse findByBusinessUnitCode(String buCode) {
        DbWarehouse db = DbWarehouse.find("businessUnitCode = ?1 and archivedAt is null", buCode).firstResult();
        return db != null ? mapToDomain(db) : null;
    }

    @Override
    public List<DomainWarehouse> findAllActive() {
        return DbWarehouse.<DbWarehouse>find("archivedAt is null").list().stream()
            .map(this::mapToDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long countActiveByLocation(String location) {
        return DbWarehouse.count("location = ?1 and archivedAt is null", location);
    }

    @Override
    public boolean existsByBusinessUnitCode(String buCode) {
        return DbWarehouse.count("businessUnitCode = ?1 and archivedAt is null", buCode) > 0;
    }

    private Optional<DbWarehouse> findDbById(Long id) {
        return DbWarehouse.find("id = ?1 and archivedAt is null", id).firstResultOptional();
    }

    private void mapToDb(DomainWarehouse source, DbWarehouse target) {
        target.businessUnitCode = source.getBusinessUnitCode();
        target.location = source.getLocation();
        target.capacity = source.getCapacity();
        target.stock = source.getStock();
        target.creationAt = source.getCreationAt();
        target.archivedAt = source.getArchivedAt();
    }

    private DomainWarehouse mapToDomain(DbWarehouse db) {
        DomainWarehouse dw = new DomainWarehouse();
dw.setId(id);

return dw;
    }
}
