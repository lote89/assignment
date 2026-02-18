package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions.WarehouseCapacityExceededException;
import com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions.WarehouseDomainException;
import com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions.WarehouseLimitExceededException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;
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
    public void create(DomainWarehouse warehouse) {
        // 1. Business Unit uniqueness check
        if (warehouseStore.existsByBusinessUnitCode(warehouse.getBusinessUnitCode())) {
            throw new WarehouseDomainException("Business unit code already exists: " + warehouse.getBusinessUnitCode());
        }

        // 2. Validate location exists (no hardcoding, uses gateway)
        Location location = locationResolver.resolveByIdentifier(warehouse.getLocation());

        // 3. Validate max warehouses for this location
        long activeCount = warehouseStore.countActiveByLocation(warehouse.getLocation());
        int maxWarehouses = location.getMaxNumberOfWarehouses();

        if (activeCount >= maxWarehouses) {
            throw new WarehouseLimitExceededException(
                "Max warehouses reached for location " + warehouse.getLocation() +
                " (limit=" + maxWarehouses + ")"
            );
        }

        // 4. Validate capacity for this location
        int maxCapacity = location.getMaxCapacity();

        if (warehouse.getCapacity() > maxCapacity) {
            throw new WarehouseCapacityExceededException(
                "Capacity " + warehouse.getCapacity() +
                " exceeds limit " + maxCapacity +
                " for location " + warehouse.getLocation()
            );
        }

        // 5. Stock vs capacity check
        if (warehouse.getStock() > warehouse.getCapacity()) {
            throw new WarehouseDomainException("Stock exceeds capacity");
        }

        // 6. Create warehouse
        warehouse.setCreationAt(ZonedDateTime.now());
        warehouseStore.create(warehouse);
    }
}
