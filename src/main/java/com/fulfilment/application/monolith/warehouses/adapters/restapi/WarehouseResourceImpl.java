package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public ApiWarehouse createANewWarehouseUnit(@Valid ApiWarehouse apiWarehouse) {

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

        Warehouse domainWarehouse = new Warehouse(
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
    public List<ApiWarehouse> listAllWarehouseUnits() {
        return repo.findAllActive()
                .stream()
                .map(this::mapToApi)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiWarehouse getAWarehouseUnitByID(@NotNull @PathParam("id") String id) {
        Warehouse domain = repo.findByIdOptional(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        return mapToApi(domain);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public ApiWarehouse replaceWarehouse(@PathParam("id") String id, @Valid ApiWarehouse newApiWarehouse) {

        Warehouse old = repo.findByIdOptional(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        String newBu = newApiWarehouse.getId();
        if (!newBu.equals(old.businessUnitCode()) && repo.existsByBusinessUnitCode(newBu)) {
            throw new RuntimeException("Business unit code conflict");
        }

        Location loc = locationGateway.resolveByIdentifier(newApiWarehouse.getLocation())
                .orElseThrow(() -> new RuntimeException("Invalid location"));

        if (newApiWarehouse.getCapacity() > loc.maxCapacity()) {
            throw new RuntimeException("Capacity exceeds location maximum");
        }

        if (newApiWarehouse.getCapacity() < newApiWarehouse.getStock()) {
            throw new RuntimeException("Capacity cannot be lower than stock");
        }

        if (newApiWarehouse.getCapacity() < old.stock()) {
            throw new RuntimeException("New capacity cannot accommodate previous stock");
        }

        if (!newApiWarehouse.getStock().equals(old.stock())) {
            throw new RuntimeException("Stock must match previous warehouse");
        }

        old.updateFrom(new Warehouse(
                old.id(),
                newBu,
                loc.identification(),
                newApiWarehouse.getCapacity(),
                newApiWarehouse.getStock(),
                old.creationAt()
        ));

        repo.update(old);
        return mapToApi(old);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void archiveAWarehouseUnitByID(@PathParam("id") String id) {
        Warehouse domain = repo.findByIdOptional(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        domain.archive();
        repo.update(domain);
    }

    private ApiWarehouse mapToApi(Warehouse domain) {
        ApiWarehouse api = new ApiWarehouse();
        api.setId(domain.businessUnitCode());
        api.setLocation(domain.locationId());
        api.setCapacity(domain.capacity());
        api.setStock(domain.stock());
        return api;
    }
}

