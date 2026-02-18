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
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/warehouses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WarehouseResourceImpl implements WarehouseResource {

    @Inject WarehouseStore repo;

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
    public com.warehouse.api.beans.Warehouse createANewWarehouseUnit(@Valid com.warehouse.api.beans.Warehouse request) {
        Warehouse domainWarehouse = new Warehouse(
                null,
                request.getBuCode(),  
                request.getLocation(),
                request.getCapacity(),
                request.getStock(),
                ZonedDateTime.now(),
                null
        );
        repo.create(domainWarehouse);
        return toApiWarehouse(domainWarehouse);
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

        existing.setBusinessUnitCode(request.getBuCode());  
        existing.setLocation(request.getLocation());
        existing.setCapacity(request.getCapacity());
        existing.setStock(request.getStock());

        repo.update(existing);
        return Response.ok(toApiWarehouse(existing)).build();
    }

    
    @Override
    @DELETE
    @Path("/{id}")
    public void archiveAWarehouseUnitByID(String id) {  
        Long longId = Long.parseLong(id);
        Optional<Warehouse> warehouseOpt = repo.findById(longId);
        if (warehouseOpt.isEmpty()) {
            throw new NotFoundException("Warehouse not found: " + id);
        }
        Warehouse warehouse = warehouseOpt.get();
        warehouse.archive();
        repo.update(warehouse);
        
    }

    private com.warehouse.api.beans.Warehouse toApiWarehouse(Warehouse w) {
        com.warehouse.api.beans.Warehouse api = new com.warehouse.api.beans.Warehouse();
        api.setId(w.getId().toString());  
        api.setBuCode(w.getBusinessUnitCode());  
        api.setLocation(w.getLocation());
        api.setCapacity(w.getCapacity());
        api.setStock(w.getStock());
        return api;
    }
}
