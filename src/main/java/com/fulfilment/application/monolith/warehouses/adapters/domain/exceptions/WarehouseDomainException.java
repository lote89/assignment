package com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions;

public class WarehouseDomainException extends RuntimeException {

    public WarehouseDomainException(String message) {
        super(message);
    }

    public WarehouseDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
