package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.domain.models.DomainWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
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
    @GET
    @Path("/{id}")
    public com.warehouse.api.beans.Warehouse getAWarehouseUnitByID(@PathParam("id") String id) {
        try {
            Long longId = Long.parseLong(id);
            Optional<DomainWarehouse> warehouseOpt = repo.findById(longId);
            return warehouseOpt.map(this::toApiWarehouse).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

   @Override
   @POST
   @Path("/")
   public com.warehouse.api.beans.Warehouse createANewWarehouseUnit(@Valid com.warehouse.api.beans.Warehouse request) {
    
    Warehouse domainWarehouse = new Warehouse(
        null, request.getBusinessUnitCode(), request.getLocation(),
        request.getCapacity(), request.getStock(), ZonedDateTime.now(), null
    );
    repo.create(domainWarehouse);
    return toApiWarehouse(domainWarehouse);
    }

    @Override
    @PUT
    @Path("/{id}")
    public com.warehouse.api.beans.Warehouse replaceWarehouseUnit(@PathParam("id") String id, @Valid com.warehouse.api.beans.Warehouse request) {
        try {
            Long longId = Long.parseLong(id);
            Optional<DomainWarehouse> existingOpt = repo.findById(longId);
            if (existingOpt.isEmpty()) {
                throw new NotFoundException("Warehouse not found: " + id);
            }
            DomainWarehouse existing = existingOpt.get();

            existing.setBusinessUnitCode(request.getBusinessUnitCode());
            existing.setLocation(request.getLocation());
            existing.setCapacity(request.getCapacity());
            existing.setStock(request.getStock());

            repo.update(existing);
            return toApiWarehouse(existing);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid ID format");
        }
    }

    @Override
    @DELETE
    @Path("/{id}")
    public void archiveAWarehouseUnitByID(@PathParam("id") String id) {
        try {
            Long longId = Long.parseLong(id);
            repo.archive(longId);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid ID format");
        }
    }

    private com.warehouse.api.beans.Warehouse toApiWarehouse(DomainWarehouse w) {
        com.warehouse.api.beans.Warehouse api = new com.warehouse.api.beans.Warehouse();
        if (w.getId() != null) {
            api.setId(w.getId().toString());
        }
        api.setBusinessUnitCode(w.getBusinessUnitCode());
        api.setLocation(w.getLocation());
        api.setCapacity(w.getCapacity());
        api.setStock(w.getStock());
        return api;
    }
}
