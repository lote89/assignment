package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
            throw new WebApplicationException("Business unit code exists", 409);
        }
        Location loc = locationGateway.resolveByIdentifier(apiWh.getLocation())
                .orElseThrow(() -> new WebApplicationException("Invalid location", 404));
        if (repo.countActiveByLocation(loc.identification()) >= loc.maxNumberOfWarehouses()) {
            throw new WebApplicationException("Max warehouses reached", 409);
        }
        int cap = apiWh.getCapacity();
        int stock = apiWh.getStock();
        if (cap > loc.maxCapacity() || cap < stock) {
            throw new WebApplicationException("Invalid capacity/stock", 422);
        }
        Warehouse domain = mapToDomain(apiWh, loc.identification());
        repo.create(domain);
        return mapToApi(domain);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Warehouse> listAllWarehouseUnits() {
        return repo.findAllActive().stream().map(this::mapToApi).collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Warehouse getAWarehouseUnitByID(@NotNull @PathParam("id") String id) {
        Warehouse domain = repo.findById(Long.parseLong(id))
                .orElseThrow(() -> new WebApplicationException("Warehouse not found", 404));
        return mapToApi(domain);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Warehouse replaceWarehouse(@PathParam("id") String id, @Valid Warehouse newApi) {
        Warehouse old = repo.findById(Long.parseLong(id))
                .orElseThrow(() -> new WebApplicationException("Warehouse not found", 404));
        String newBu = newApi.getId();
        if (!newBu.equals(old.businessUnitCode()) && repo.existsByBusinessUnitCode(newBu)) {
            throw new WebApplicationException("Business unit code conflict", 409);
        }
        Location loc = locationGateway.resolveByIdentifier(newApi.getLocation())
                .orElseThrow(() -> new WebApplicationException("Invalid location", 404));
        int newCap = newApi.getCapacity();
        int newStock = newApi.getStock();
        if (newCap > loc.maxCapacity() || newCap < newStock ||
            newCap < old.stock() || newStock != old.stock()) {
            throw new WebApplicationException("Invalid capacity/stock or mismatch", 422);
        }
        Warehouse updated = new Warehouse(old.id(), newBu, newApi.getLocation(), newCap, newStock);
        repo.update(updated);
        return mapToApi(updated);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void archiveAWarehouseUnitByID(@PathParam("id") String id) {
        Warehouse domain = repo.findById(Long.parseLong(id))
                .orElseThrow(() -> new WebApplicationException("Warehouse not found", 404));
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
