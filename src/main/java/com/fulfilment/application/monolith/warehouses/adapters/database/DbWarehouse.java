package com.fulfilment.application.monolith.warehouses.adapters.database;

import jakarta.persistence.*;
import java.time.ZonedDateTime;  // Match domain

@Entity
@Table(name = "warehouse")
@Cacheable
public class DbWarehouse {
  @Id @GeneratedValue 
  public Long id;

  public String businessUnitCode;
  public String location;
  public Integer capacity;
  public Integer stock;
  public ZonedDateTime creationAt;  // Match domain
  public ZonedDateTime archivedAt;

  public DbWarehouse() {}
}
