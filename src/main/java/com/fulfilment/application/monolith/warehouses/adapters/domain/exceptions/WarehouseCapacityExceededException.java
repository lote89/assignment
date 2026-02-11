package com.fulfilment.application.monolith.warehouse.adapters.domain.exceptions;

public class WarehouseCapacityExceededException extends WarehouseDomainException {
    public WarehouseCapacityExceededException(String message) {
        super(message);
    }
}
