package com.challenge;

import com.challenge.repository.UserRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/test")
public class GreetingResource {
    @Inject
    UserRepository userRepository;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("User")
    public Response checkConnection() {
        try {
            long count = userRepository.count();
            return Response.ok("Database connection is OK. User count: " + count).build();
        } catch (Exception e) {
            return Response.serverError().entity("Database connection failed: " + e.getMessage()).build();
        }
    }
}
