package com.fulfilment.application.monolith.warehouses.adapters.domain.exceptions;

public class LocationNotFoundException extends WarehouseDomainException {
    public LocationNotFoundException(String message) {
        super(message);
    }
}
