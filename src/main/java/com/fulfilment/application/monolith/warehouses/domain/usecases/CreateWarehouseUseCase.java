package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.ZonedDateTime;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseDomainException;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  
private final WarehouseStore warehouseStore;
public CreateWarehouseUseCase(WarehouseStore warehouseStore) {
  this.warehouseStore = warehouseStore;
}

@Override
public void create(Warehouse warehouse) {
  // BU check
  if (warehouseStore.existsByBusinessUnitCode(warehouse.businessUnitCode)) {
    throw new WarehouseDomainException("Business unit code already exists: " + warehouse.businessUnitCode);
  }

  // Location/Max (hardcode or skip validation for compile)
  long activeCount = warehouseStore.countActiveByLocation(warehouse.location);
  if (activeCount >= 5) {  // Temp hardcoded max
    throw new WarehouseDomainException("Max warehouses reached at location: " + warehouse.location);
  }

  // Capacity/Stock
  if (warehouse.capacity > 1000) {  // Temp max
    throw new WarehouseDomainException("Capacity exceeded: " + warehouse.capacity);
  }
  if (warehouse.stock > warehouse.capacity) {
    throw new WarehouseDomainException("Stock exceeds capacity");
  }

  warehouse.creationAt = ZonedDateTime.now();
  warehouseStore.create(warehouse);
}

}
