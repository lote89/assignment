package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseCapacityExceededException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseDomainException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseLimitExceededException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
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
    public void replace(Warehouse newWarehouse) {
        // 1. Find existing warehouse
        Warehouse oldWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
        if (oldWarehouse == null) {
            throw new WarehouseDomainException("Warehouse not found: " + newWarehouse.businessUnitCode);
        }

        // 2. Validate location exists
        Location location = locationResolver.resolveByIdentifier(newWarehouse.location);

        // 3. Validate capacity against location limit
        int maxCapacity = location.getMaxCapacity();
        if (newWarehouse.capacity > maxCapacity) {
            throw new WarehouseCapacityExceededException(
                "Capacity " + newWarehouse.capacity +
                " exceeds limit " + maxCapacity +
                " for location " + newWarehouse.location
            );
        }

        // 4. Stock consistency checks
        if (newWarehouse.capacity < oldWarehouse.stock) {
            throw new WarehouseDomainException("Capacity cannot accommodate stock: " + newWarehouse.capacity);
        }

        if (!newWarehouse.stock.equals(oldWarehouse.stock)) {
            throw new WarehouseDomainException("Stock mismatch: " + newWarehouse.stock);
        }

        // 5. Archive old, update with new
        archiveUseCase.archive(oldWarehouse);
        newWarehouse.creationAt = ZonedDateTime.now();
        warehouseStore.update(newWarehouse);
    }
}
