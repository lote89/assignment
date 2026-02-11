package com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions;

public class WarehouseCapacityExceededException extends WarehouseDomainException {
    public WarehouseCapacityExceededException(String message) {
        super(message);
    }
}
