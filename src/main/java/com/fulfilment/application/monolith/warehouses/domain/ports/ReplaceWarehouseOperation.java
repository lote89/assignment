package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;

public interface ReplaceWarehouseOperation {
  void replace(DomainWarehouse warehouse);
}
