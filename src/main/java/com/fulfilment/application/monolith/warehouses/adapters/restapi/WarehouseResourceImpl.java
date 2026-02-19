package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/warehouses")
@Produces(MediaType.APPLICATION_JSON)
public class WarehouseResourceImpl {

    @Path("/test")
    public Response test() {
        return Response.ok().entity("{\"status\":\"OK\"}").build();
    }

    // Empty stubs for all expected endpoints
    @Path("/{id}")
    public Response getWarehouse() {
        return Response.ok().entity("{}").build();
    }

    @Path("/business-unit/{code}")
    public Response getByBusinessUnitCode() {
        return Response.ok().entity("[]").build();
    }

    @Path("/active")
    public Response getActiveWarehouses() {
        return Response.ok().entity("[]").build();
    }

    @Path("/{id}/archive")
    public Response archiveWarehouse() {
        return Response.ok().entity("{}").build();
    }
}
