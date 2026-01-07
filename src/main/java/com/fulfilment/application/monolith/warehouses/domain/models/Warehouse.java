package com.fulfilment.application.monolith.warehouses.domain.models;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "warehouse")  // Adjust table name if needed
public class Warehouse extends PanacheEntityBase {  // Enables Panache CRUD


  @Id @GeneratedValue  
  public Long id;
  @Column(unique = true, nullable = false)
  public String businessUnitCode;

  @Column(nullable = false)
  public String location;

  @Column(nullable = false)
  public Integer capacity;

  @Column(nullable = false)
  public Integer stock;

  @Column(nullable = false)
  public ZonedDateTime creationAt;

  public ZonedDateTime archivedAt;  // Null = active

  // Business method for active check (used in queries/mappers)
  public boolean isActive() {
    return archivedAt == null;
  }
}
