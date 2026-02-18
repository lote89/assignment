package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;  
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
    public List<Warehouse> listAllWarehousesUnits() {  
        return repo.findAllActive().stream()
                .map(this::toApiWarehouse)
                .collect(Collectors.toList());
    }

    @Override
    @GET
    @Path("/{id}")
    public Warehouse getAWarehouseUnitByID(@PathParam("id") String id) {  
        Long longId = Long.parseLong(id);
        return repo.findById(longId).orElse(null);  
    }

    @Override
    @POST
    @Path("/")
    public Warehouse createANewWarehouseUnit(@Valid Warehouse request) {  
        Warehouse domainWarehouse = new Warehouse(
                null,
                request.getBusinessUnitCode(),  
                request.getLocation(),
                request.getCapacity(),
                request.getStock(),
                ZonedDateTime.now(),
                null
        );
        repo.create(domainWarehouse);
        return toApiWarehouse(domainWarehouse);
    }

    // **FIX 2: String id parameter (Line 65 error)**
    @Override
    @PUT
    @Path("/{id}")
    public Warehouse replaceWarehouseUnit(@PathParam("id") String id, @Valid Warehouse request) {
        Long longId = Long.parseLong(id);
        Optional<Warehouse> existingOpt = repo.findById(longId);
        if (existingOpt.isEmpty()) {
            throw new NotFoundException("Warehouse not found: " + id);
        }
        Warehouse existing = existingOpt.get();
        existing.setBusinessUnitCode(request.getBusinessUnitCode());
        existing.setLocation(request.getLocation());
        existing.setCapacity(request.getCapacity());
        existing.setStock(request.getStock());
        repo.update(existing);
        return toApiWarehouse(existing);  
    }

    // **FIX 3: String id parameter (Line 19, 83 errors)**
    @Override
    @DELETE
    @Path("/{id}")
    public void archiveAWarehouseUnitByID(@PathParam("id") String id) {  
        Long longId = Long.parseLong(id);
        Optional<Warehouse> warehouseOpt = repo.findById(longId);
        if (warehouseOpt.isEmpty()) {
            throw new NotFoundException("Warehouse not found: " + id);
        }
        Warehouse warehouse = warehouseOpt.get();
        warehouse.archive();
        repo.update(warehouse);
    }

    private Warehouse toApiWarehouse(Warehouse w) {  
        Warehouse api = new Warehouse();
        api.setId(w.getId());
        api.setBusinessUnitCode(w.getBusinessUnitCode());  
        api.setLocation(w.getLocation());
        api.setCapacity(w.getCapacity());
        api.setStock(w.getStock());
        return api;
    }
}
