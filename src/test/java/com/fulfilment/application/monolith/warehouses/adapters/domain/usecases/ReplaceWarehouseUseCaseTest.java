package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseCapacityExceededException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseDomainException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReplaceWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;
    private ArchiveWarehouseUseCase archiveUseCase;
    private LocationResolver locationResolver;
    private ReplaceWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        warehouseStore = mock(WarehouseStore.class);
        archiveUseCase = mock(ArchiveWarehouseUseCase.class);
        locationResolver = mock(LocationResolver.class);
        useCase = new ReplaceWarehouseUseCase(warehouseStore, archiveUseCase, locationResolver);
    }

    @Test
    void shouldReplaceWarehouseWhenValid() {
        Warehouse oldWarehouse = new Warehouse("BU-1", "AMSTERDAM-001", 50, 10);
        Warehouse newWarehouse = new Warehouse("BU-1", "AMSTERDAM-001", 60, 10);
        Location location = new Location("AMSTERDAM-001", 5, 100);

        when(warehouseStore.findByBusinessUnitCode("BU-1")).thenReturn(oldWarehouse);
        when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(location);

        useCase.replace(newWarehouse);

        verify(archiveUseCase).archive(oldWarehouse);
        verify(warehouseStore).update(newWarehouse);
        assertNotNull(newWarehouse.creationAt);
    }

    @Test
    void shouldFailWhenWarehouseNotFound() {
        Warehouse newWarehouse = new Warehouse("BU-1", "AMSTERDAM-001", 60, 10);

        when(warehouseStore.findByBusinessUnitCode("BU-1")).thenReturn(null);

        assertThrows(WarehouseDomainException.class, () -> useCase.replace(newWarehouse));
    }

    @Test
    void shouldFailWhenCapacityExceededForLocation() {
        Warehouse oldWarehouse = new Warehouse("BU-1", "AMSTERDAM-001", 50, 10);
        Warehouse newWarehouse = new Warehouse("BU-1", "AMSTERDAM-001", 150, 10);
        Location location = new Location("AMSTERDAM-001", 5, 100);

        when(warehouseStore.findByBusinessUnitCode("BU-1")).thenReturn(oldWarehouse);
        when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(location);

        assertThrows(WarehouseCapacityExceededException.class, () -> useCase.replace(newWarehouse));
    }

    @Test
    void shouldFailWhenStockMismatch() {
        Warehouse oldWarehouse = new Warehouse("BU-1", "AMSTERDAM-001", 50, 10);
        Warehouse newWarehouse = new Warehouse("BU-1", "AMSTERDAM-001", 60, 5);
        Location location = new Location("AMSTERDAM-001", 5, 100);

        when(warehouseStore.findByBusinessUnitCode("BU-1")).thenReturn(oldWarehouse);
        when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(location);

        assertThrows(WarehouseDomainException.class, () -> useCase.replace(newWarehouse));
    }
}
