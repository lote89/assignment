package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;

public interface CreateWarehouseOperation {
  void create(DomainWarehouse warehouse);
}
