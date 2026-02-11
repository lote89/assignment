package com.fulfilment.application.monolith.warehouse.adapters.domain.exceptions;

public class WarehouseDomainException extends RuntimeException {

    public WarehouseDomainException(String message) {
        super(message);
    }

    public LocationNotFoundException(String message) {
        super(message);
    }

     public WarehouseCapacityExceededException(String message) {
        super(message);
    }

    public WarehouseLimitExceededException(String message) {
        super(message);
    }
}
