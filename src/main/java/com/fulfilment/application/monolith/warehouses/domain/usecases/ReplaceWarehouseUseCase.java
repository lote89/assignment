package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions.WarehouseCapacityExceededException;
import com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions.WarehouseDomainException;
import com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions.WarehouseLimitExceededException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.ZonedDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

    private final WarehouseStore warehouseStore;
    private final ArchiveWarehouseUseCase archiveUseCase;
    private final LocationResolver locationResolver;

    public ReplaceWarehouseUseCase(WarehouseStore warehouseStore,
                                   ArchiveWarehouseUseCase archiveUseCase,
                                   LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.archiveUseCase = archiveUseCase;
        this.locationResolver = locationResolver;
    }

    @Override
    public void replace(DomainWarehouse newWarehouse) {
        // 1. Find existing warehouse
        DomainWarehouse oldWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.getBusinessUnitCode());
        if (oldWarehouse == null) {
            throw new WarehouseDomainException("Warehouse not found: " + newWarehouse.getBusinessUnitCode());
        }

        // 2. Validate location exists
        Location location = locationResolver.resolveByIdentifier(newWarehouse.getLocation());

        // 3. Validate capacity against location limit
        int maxCapacity = location.getMaxCapacity();
        if (newWarehouse.getCapacity() > maxCapacity) {
            throw new WarehouseCapacityExceededException(
                "Capacity " + newWarehouse.getCapacity() +
                " exceeds limit " + maxCapacity +
                " for location " + newWarehouse.getLocation()
            );
        }

        // 4. Stock consistency checks
        if (newWarehouse.getCapacity() < oldWarehouse.getStock()) {
            throw new WarehouseDomainException("Capacity cannot accommodate stock: " + newWarehouse.getCapacity());
        }

        if (!newWarehouse.getStock().equals(oldWarehouse.getStock())) {
            throw new WarehouseDomainException("Stock mismatch: " + newWarehouse.getStock());
        }

        // 5. Archive old, update with new
        archiveUseCase.archive(oldWarehouse);
        newWarehouse.setCreationAt(ZonedDateTime.now());
        warehouseStore.update(newWarehouse);
    }
}
