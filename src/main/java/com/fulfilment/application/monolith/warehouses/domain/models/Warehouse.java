package com.fulfilment.application.monolith.warehouses.domain.models;

public record Warehouse(Long id, String businessUnitCode, String locationId, int capacity, int stock) {}
