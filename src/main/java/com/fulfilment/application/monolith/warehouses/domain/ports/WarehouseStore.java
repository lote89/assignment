package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import java.util.List;
import java.util.Optional;

public interface WarehouseStore {
  void create(Warehouse warehouse);

  void update(Warehouse warehouse);

  void remove(Warehouse warehouse);

  Warehouse findByBusinessUnitCode(String buCode);
  boolean existsByBusinessUnitCode(String code);
  long countActiveByLocation(String location);
  List<Warehouse> findAllActive();
  Optional<Warehouse> findById(Long id);
}
