package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions.WarehouseCapacityExceededException;
import com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions.WarehouseDomainException;
import com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions.WarehouseLimitExceededException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.ZonedDateTime;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

    private final WarehouseStore warehouseStore;
    private final LocationResolver locationResolver;

    public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.locationResolver = locationResolver;
    }

    @Override
    public void create(Warehouse warehouse) {
        // 1. Business Unit uniqueness check
        if (warehouseStore.existsByBusinessUnitCode(warehouse.businessUnitCode)) {
            throw new WarehouseDomainException("Business unit code already exists: " + warehouse.businessUnitCode);
        }

        // 2. Validate location exists (no hardcoding, uses gateway)
        Location location = locationResolver.resolveByIdentifier(warehouse.location);

        // 3. Validate max warehouses for this location
        long activeCount = warehouseStore.countActiveByLocation(warehouse.location);
        int maxWarehouses = location.getMaxNumberOfWarehouses();

        if (activeCount >= maxWarehouses) {
            throw new WarehouseLimitExceededException(
                "Max warehouses reached for location " + warehouse.location +
                " (limit=" + maxWarehouses + ")"
            );
        }

        // 4. Validate capacity for this location
        int maxCapacity = location.getMaxCapacity();

        if (warehouse.capacity > maxCapacity) {
            throw new WarehouseCapacityExceededException(
                "Capacity " + warehouse.capacity +
                " exceeds limit " + maxCapacity +
                " for location " + warehouse.location
            );
        }

        // 5. Stock vs capacity check
        if (warehouse.stock > warehouse.capacity) {
            throw new WarehouseDomainException("Stock exceeds capacity");
        }

        // 6. Create warehouse
        warehouse.creationAt = ZonedDateTime.now();
        warehouseStore.create(warehouse);
    }
}
