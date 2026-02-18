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
    @GET @Path("/")
    public List<com.warehouse.api.beans.Warehouse> listAllWarehousesUnits() {
        return repo.findAllActive().stream().map(this::toApiWarehouse).collect(Collectors.toList());
    }

    
    @Override
    @POST @Path("/")
    public com.warehouse.api.beans.Warehouse createANewWarehouseUnit(@Valid com.warehouse.api.beans.Warehouse request) {
        Warehouse domain = new Warehouse(null, request.getBusinessUnitCode(), request.getLocation(), 
            request.getCapacity(), request.getStock(), ZonedDateTime.now(), null);
        repo.create(domain);
        return toApiWarehouse(domain);
    }

    @Override
    @PUT @Path("/{id}")
    public Response replaceWarehouseUnit(@PathParam("id") Long id, @Valid com.warehouse.api.beans.Warehouse request) {
        Optional<Warehouse> opt = repo.findById(id);
        if (opt.isEmpty()) throw new NotFoundException();
        Warehouse w = opt.get();
        w.setBusinessUnitCode(request.getBusinessUnitCode());
        w.setLocation(request.getLocation());
        w.setCapacity(request.getCapacity());
        w.setStock(request.getStock());
        repo.update(w);
        return Response.ok(toApiWarehouse(w)).build();
    }

    
    @Override
    @DELETE @Path("/{id}")
    public void archiveAWarehouseUnitByID(@PathParam("id") String id) {  
        Long lid = Long.parseLong(id);
        Optional<Warehouse> opt = repo.findById(lid);
        if (opt.isEmpty()) throw new NotFoundException();
        Warehouse w = opt.get();
        w.archive();
        repo.update(w);
    }

    private com.warehouse.api.beans.Warehouse toApiWarehouse(Warehouse w) {
        com.warehouse.api.beans.Warehouse api = new com.warehouse.api.beans.Warehouse();
        api.setId(w.getId().toString());  
        api.setBusinessUnitCode(w.getBusinessUnitCode());  
        api.setLocation(w.getLocation());
        api.setCapacity(w.getCapacity());
        api.setStock(w.getStock());
        return api;  // 
    }
}
