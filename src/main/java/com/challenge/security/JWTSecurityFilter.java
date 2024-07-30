package com.challenge.security;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class JWTSecurityFilter implements ContainerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(JWTSecurityFilter.class);

    @Inject
    JWTParser jwtParser;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        LOG.info("Entro a filter jwt parte 1");

        // Excluir el path de login de la validación de JWT
        String path = requestContext.getUriInfo().getPath();
        if (requestContext.getUriInfo().getPath().contains("/user/login")) {
            return; // Omite la validación de JWT para esta ruta
        }
        LOG.info("Entro a filter jwt parte 2");

        String authHeader = requestContext.getHeaderString("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer".length()).trim();
            try {
                jwtParser.parse(token);
                LOG.info("JWT Token is valid.");
            } catch (ParseException e) {
                LOG.error("Invalid JWT Token: {}", e.getMessage());
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        } else {
            LOG.error("Missing or invalid Authorization header.");
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
