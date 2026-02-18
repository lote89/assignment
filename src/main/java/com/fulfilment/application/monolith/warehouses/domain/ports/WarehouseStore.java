package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;
import java.util.List;
import java.util.Optional;

public interface WarehouseStore {
  void create(DomainWarehouse warehouse);

  void update(DomainWarehouse warehouse);

  void archive(Long id);

  Warehouse findByBusinessUnitCode(String buCode);
  boolean existsByBusinessUnitCode(String code);
  long countActiveByLocation(String location);
  List<DomainWarehouse> findAllActive();
  Optional<DomainWarehouse> findById(Long id);
}
