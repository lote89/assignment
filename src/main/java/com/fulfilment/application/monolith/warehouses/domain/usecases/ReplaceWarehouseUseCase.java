package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.ZonedDateTime;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseDomainException;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {
  private final WarehouseStore warehouseStore;
  private final ArchiveWarehouseUseCase archiveUseCase;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, ArchiveWarehouseUseCase archiveUseCase) {
    this.warehouseStore = warehouseStore;
    this.archiveUseCase = archiveUseCase;
  }

  @Override
public void replace(Warehouse newWarehouse) {
  Warehouse oldWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
  if (oldWarehouse == null) {
    throw new WarehouseDomainException("Warehouse not found: " + newWarehouse.businessUnitCode);
  }

  if (newWarehouse.capacity < oldWarehouse.stock) {
    throw new WarehouseDomainException("Capacity cannot accommodate stock: " + newWarehouse.capacity);
  }
  if (!newWarehouse.stock.equals(oldWarehouse.stock)) {
    throw new WarehouseDomainException("Stock mismatch: " + newWarehouse.stock);
  }

  archiveUseCase.archive(oldWarehouse);
  newWarehouse.creationAt = ZonedDateTime.now();
  warehouseStore.update(newWarehouse);
}
}
