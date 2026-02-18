package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Path("/warehouses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WarehouseResourceImpl implements WarehouseResource {

    @Inject
    WarehouseStore repo;

    @Override
    @GET
    @Path("/")
    public List<com.warehouse.api.beans.Warehouse> listAllWarehousesUnits() {
        
        return repo.findAllActive().stream()
                .map(this::toApiWarehouse)
                .collect(Collectors.toList());
    }

    @Override
    @POST
    @Path("/")
    public Response createANewWarehouseUnit(@Valid com.warehouse.api.beans.Warehouse request) {
        
        Warehouse domainWarehouse = new Warehouse(
                null,
                request.getBusinessUnitCode(),
                request.getLocation(),
                request.getCapacity(),
                request.getStock(),
                ZonedDateTime.now(),
                null  // ADD: archivedAt
        );

        repo.create(domainWarehouse);  

        return Response.status(Response.Status.CREATED)
                .entity(toApiWarehouse(domainWarehouse))
                .build();
    }

    @Override
    @PUT
    @Path("/{id}")
    public Response replaceWarehouseUnit(@PathParam("id") Long id, @Valid com.warehouse.api.beans.Warehouse request) {
        
        Optional<Warehouse> existingOpt = repo.findById(id);
        if (existingOpt.isEmpty()) {
            throw new NotFoundException("Warehouse not found: " + id);
        }
        Warehouse existing = existingOpt.get();

        // Update fields
        existing.setBusinessUnitCode(request.getBusinessUnitCode());
        existing.setLocation(request.getLocation());
        existing.setCapacity(request.getCapacity());
        existing.setStock(request.getStock());

        repo.update(existing);  

        return Response.ok(toApiWarehouse(existing)).build();
    }

    @Override
    @DELETE
    @Path("/{id}")
    public Response archiveWarehouseUnit(@PathParam("id") Long id) {
        Optional<Warehouse> warehouseOpt = repo.findById(id);
        if (warehouseOpt.isEmpty()) {
            throw new NotFoundException("Warehouse not found: " + id);
        }
        Warehouse warehouse = warehouseOpt.get();

        warehouse.archive();
        repo.update(warehouse);  

        return Response.noContent().build();
    }

    private com.warehouse.api.beans.Warehouse toApiWarehouse(Warehouse w) {
        com.warehouse.api.beans.Warehouse api = new com.warehouse.api.beans.Warehouse();
        api.setId(w.getId());
        api.setBusinessUnitCode(w.getBusinessUnitCode());
        api.setLocation(w.getLocation());
        api.setCapacity(w.getCapacity());
        api.setStock(w.getStock());
        api.setCreationAt(w.getCreationAt());
        return api;
    }
}
