package com.fulfilment.application.monolith.warehouses.domain.models;

public class Location {  // POJO, not entity
  public String identification;
  public int maxNumberOfWarehouses;
  public int maxCapacity;

  public Location(String identification, int maxNumberOfWarehouses, int maxCapacity) {
    this.identification = identification;
    this.maxNumberOfWarehouses = maxNumberOfWarehouses;
    this.maxCapacity = maxCapacity;
  }

  // Add method for LocationGateway/UseCases
  public String identifier() {
    return identification;
  }

  public int getMaxNumberOfWarehouses() { return maxNumberOfWarehouses; }
  public int getMaxCapacity() { return maxCapacity; }
}
