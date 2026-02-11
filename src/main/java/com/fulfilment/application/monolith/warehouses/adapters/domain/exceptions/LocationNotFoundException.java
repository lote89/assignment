package com.fulfilment.application.monolith.warehouse.adapters.domain.exceptions;

public class LocationNotFoundException extends WarehouseDomainException {
    public LocationNotFoundException(String message) {
        super(message);
    }
}
