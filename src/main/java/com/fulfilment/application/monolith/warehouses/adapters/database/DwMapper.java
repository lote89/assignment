package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;
import java.time.ZonedDateTime;

public class DwMapper {
    public static DomainWarehouse toDomain(DbWarehouse db) {
    return new DomainWarehouse(
        db.id.toString(),  // Long → String
        db.businessUnitCode,
        db.location,
        db.capacity,
        db.stock,
        db.creationAt,
        db.archivedAt
    );
}

    
}
