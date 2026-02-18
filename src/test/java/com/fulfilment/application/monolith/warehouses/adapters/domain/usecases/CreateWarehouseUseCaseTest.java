package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseCapacityExceededException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseDomainException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseLimitExceededException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;
    private LocationResolver locationResolver;
    private CreateWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        warehouseStore = mock(WarehouseStore.class);
        locationResolver = mock(LocationResolver.class);
        useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);
    }

    @Test
    void shouldCreateWarehouseWhenValid() {
        DomainWarehouse warehouse = new DomainWarehouse("BU-1", "AMSTERDAM-001", 50, 10);

        Location location = new Location("AMSTERDAM-001", 5, 100);

        when(warehouseStore.existsByBusinessUnitCode("BU-1")).thenReturn(false);
        when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(location);
        when(warehouseStore.countActiveByLocation("AMSTERDAM-001")).thenReturn(2L);

        useCase.create(warehouse);

        verify(warehouseStore).create(warehouse);
        assertNotNull(warehouse.creationAt);
    }

    @Test
    void shouldFailWhenBusinessUnitExists() {
        DomainWarehouse warehouse = new DomainWarehouse("BU-1", "AMSTERDAM-001", 50, 10);

        when(warehouseStore.existsByBusinessUnitCode("BU-1")).thenReturn(true);

        assertThrows(WarehouseDomainException.class, () -> useCase.create(warehouse));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void shouldFailWhenMaxWarehousesReached() {
        DomainWarehouse warehouse = new DomainWarehouse("BU-1", "AMSTERDAM-001", 50, 10);
        Location location = new Location("AMSTERDAM-001", 2, 100);

        when(warehouseStore.existsByBusinessUnitCode("BU-1")).thenReturn(false);
        when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(location);
        when(warehouseStore.countActiveByLocation("AMSTERDAM-001")).thenReturn(2L);

        assertThrows(WarehouseLimitExceededException.class, () -> useCase.create(warehouse));
    }

    @Test
    void shouldFailWhenCapacityExceeded() {
        DomainWarehouse warehouse = new DomainWarehouse("BU-1", "AMSTERDAM-001", 150, 10);
        Location location = new Location("AMSTERDAM-001", 5, 100);

        when(warehouseStore.existsByBusinessUnitCode("BU-1")).thenReturn(false);
        when(locationResolver.resolveByIdentifier("AMSTERDAM-001")).thenReturn(location);
        when(warehouseStore.countActiveByLocation("AMSTERDAM-001")).thenReturn(1L);

        assertThrows(WarehouseCapacityExceededException.class, () -> useCase.create(warehouse));
    }
}

