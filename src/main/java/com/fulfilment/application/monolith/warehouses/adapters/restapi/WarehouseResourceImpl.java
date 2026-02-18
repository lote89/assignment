package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse; // API bean
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;

@ApplicationScoped
public class WarehouseResourceImpl implements WarehouseResource {

    @Inject
    WarehouseStore repo;

    @Inject
    LocationGateway locationGateway;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Warehouse createANewWarehouseUnit(@Valid Warehouse apiWarehouse) {

        String buCode = apiWarehouse.getId();

        if (repo.existsByBusinessUnitCode(buCode)) {
            throw new RuntimeException("Business unit code already exists");
        }

        Location loc = locationGateway.resolveByIdentifier(apiWarehouse.getLocation())
                .orElseThrow(() -> new RuntimeException("Invalid location"));

        if (repo.countActiveByLocation(loc.identification()) >= loc.maxNumberOfWarehouses()) {
            throw new RuntimeException("Maximum warehouses reached for location");
        }

        if (apiWarehouse.getCapacity() > loc.maxCapacity()) {
            throw new RuntimeException("Capacity exceeds location maximum");
        }

        if (apiWarehouse.getCapacity() < apiWarehouse.getStock()) {
            throw new RuntimeException("Capacity cannot be lower than stock");
        }

        // Create domain entity
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domainWarehouse =
                new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse(
                        null,
                        apiWarehouse.getId(),
                        loc.identification(),
                        apiWarehouse.getCapacity(),
                        apiWarehouse.getStock(),
                        ZonedDateTime.now()
                );

        repo.create(domainWarehouse);

        return mapToApi(domainWarehouse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Warehouse> listAllWarehouseUnits() {
        return repo.findAllActive()
                .stream()
                .map(this::mapToApi)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Warehouse getAWarehouseUnitByID(@NotNull @PathParam("id") String id) {
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domain =
                repo.findByIdOptional(Long.parseLong(id))
                        .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        return mapToApi(domain);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
