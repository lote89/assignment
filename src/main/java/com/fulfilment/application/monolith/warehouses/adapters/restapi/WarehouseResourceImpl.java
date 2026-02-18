package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse as ApiWarehouse;

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
    public List<ApiWarehouse> listAllWarehousesUnits() {
        return repo.listAll().stream()
                .map(this::toApiWarehouse)
                .collect(Collectors.toList());
    }

    @Override
    @POST
    @Path("/")
    public Response createANewWarehouseUnit(@Valid ApiWarehouse request) {
        Warehouse warehouse = new Warehouse(
                null,
                request.getBusinessUnitCode(),
                request.getLocation(),
                request.getCapacity(),
                request.getStock(),
                ZonedDateTime.now()
        );

        repo.persist(warehouse);

        return Response.status(Response.Status.CREATED)
                .entity(toApiWarehouse(warehouse))
                .build();
    }

    @Override
    @PUT
    @Path("/{id}")
    public Response replaceWarehouseUnit(@PathParam("id") Long id, @Valid ApiWarehouse request) {
        Warehouse existing = repo.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Warehouse not found: " + id));

        existing.setBusinessUnitCode(request.getBusinessUnitCode());
        existing.setLocation(request.getLocation());
        existing.setCapacity(request.getCapacity());
        existing.setStock(request.getStock());

        repo.persist(existing);

        return Response.ok(toApiWarehouse(existing)).build();
    }

    @Override
    @DELETE
    @Path("/{id}")
    public Response archiveWarehouseUnit(@PathParam("id") Long id) {
        Warehouse warehouse = repo.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Warehouse not found: " + id));

        warehouse.archive();
        repo.persist(warehouse);

        return Response.noContent().build();
    }

    // Mapper: Domain -> API bean
    private ApiWarehouse toApiWarehouse(Warehouse w) {
        ApiWarehouse api = new ApiWarehouse();
        api.setId(w.getId());
        api.setBusinessUnitCode(w.getBusinessUnitCode());
        api.setLocation(w.getLocation());
        api.setCapacity(w.getCapacity());
        api.setStock(w.getStock());
        api.setCreationAt(w.getCreationAt());
        return api;
    }
}
