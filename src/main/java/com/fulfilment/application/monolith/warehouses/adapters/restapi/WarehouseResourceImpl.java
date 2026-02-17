package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.WarehouseDomainException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

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
    public Warehouse createWarehouse(@Valid Warehouse apiWh) {

        String buCode = apiWh.getId();

        if (repo.existsByBusinessUnitCode(buCode)) {
            throw new WarehouseDomainException("Business unit code already exists");
        }

        Location loc = locationGateway.resolveByIdentifier(apiWh.getLocation())
                .orElseThrow(() -> new WarehouseDomainException("Invalid location"));

        if (repo.countActiveByLocation(loc.identification()) >= loc.maxNumberOfWarehouses()) {
            throw new WarehouseDomainException("Maximum warehouses reached for location");
        }

        int cap = apiWh.getCapacity();
        int stock = apiWh.getStock();

        if (cap > loc.maxCapacity()) {
            throw new WarehouseDomainException("Capacity exceeds location maximum");
        }

        if (cap < stock) {
            throw new WarehouseDomainException("Capacity cannot be lower than stock");
        }

        Warehouse domain = mapToDomain(apiWh, loc.identification());
        repo.create(domain);

        return mapToApi(domain);
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

        return repo.findById(Long.parseLong(id))
                .map(this::mapToApi)
                .orElseThrow(() -> new WarehouseDomainException("Warehouse not found"));
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Warehouse replaceWarehouse(@PathParam("id") String id, @Valid Warehouse newApi) {

        Warehouse old = repo.findById(Long.parseLong(id))
                .orElseThrow(() -> new WarehouseDomainException("Warehouse not found"));

        String newBu = newApi.getId();

        if (!newBu.equals(old.businessUnitCode())
                && repo.existsByBusinessUnitCode(newBu)) {
            throw new WarehouseDomainException("Business unit code conflict");
        }

        Location loc = locationGateway.resolveByIdentifier(newApi.getLocation())
                .orElseThrow(() -> new WarehouseDomainException("Invalid location"));

        int newCap = newApi.getCapacity();
        int newStock = newApi.getStock();

        if (newCap > loc.maxCapacity()) {
            throw new WarehouseDomainException("Capacity exceeds location maximum");
        }

        if (newCap < newStock) {
            throw new WarehouseDomainException("Capacity cannot be lower than stock");
        }

        if (newCap < old.stock()) {
            throw new WarehouseDomainException("New capacity cannot accommodate previous stock");
        }

        if (newStock != old.stock()) {
            throw new WarehouseDomainException("Stock must match previous warehouse");
        }

        Warehouse updated = new Warehouse(
                old.id(),
                newBu,
                newApi.getLocation(),
                newCap,
                newStock
        );

        repo.update(updated);

        return mapToApi(updated);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void archiveAWarehouseUnitByID(@PathParam("id") String id) {

        Warehouse domain = repo.findById(Long.parseLong(id))
                .orElseThrow(() -> new WarehouseDomainException("Warehouse not found"));

        repo.archive(domain.id());
    }

    private Warehouse mapToDomain(Warehouse api, String locId) {
        return new Warehouse(null, api.getId(), locId, api.getCapacity(), api.getStock());
    }

    private Warehouse mapToApi(Warehouse domain) {
        Warehouse api = new Warehouse();
        api.setId(domain.businessUnitCode());
        api.setLocation(domain.locationId());
        api.setCapacity(domain.capacity());
        api.setStock(domain.stock());
        return api;
    }
}
