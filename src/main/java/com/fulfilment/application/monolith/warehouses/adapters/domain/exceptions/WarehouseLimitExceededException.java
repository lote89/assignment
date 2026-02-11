package com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions;

public class WarehouseLimitExceededException extends WarehouseDomainException {
    public WarehouseLimitExceededException(String message) {
        super(message);
    }
}
