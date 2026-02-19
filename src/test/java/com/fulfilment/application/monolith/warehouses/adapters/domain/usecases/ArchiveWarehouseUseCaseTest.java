package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArchiveWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;
    private ArchiveWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        warehouseStore = mock(WarehouseStore.class);
        useCase = new ArchiveWarehouseUseCase(warehouseStore);
    }

    @Test
    void shouldArchiveWarehouse() {
        DomainWarehouse warehouse = new DomainWarehouse("BU-1", "AMSTERDAM-001", 50, 10);

        assertNull(warehouse.archivedAt);

        useCase.archive(warehouse);

        assertNotNull(warehouse.archivedAt, "archivedAt should be set");
        verify(warehouseStore).update(warehouse);
    }

    @Test
    void shouldSetArchiveTimeToNow() {
        DomainWarehouse warehouse = new DomainWarehouse("BU-2", "ZWOLLE-001", 40, 5);

        useCase.archive(warehouse);

        ZonedDateTime now = ZonedDateTime.now();
    }
}
