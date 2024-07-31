package com.challenge.security;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@Provider
public class CSRFSecurityFilter implements ContainerRequestFilter {

    @ConfigProperty(name = "quarkus.http.csrf.token.header")
    String csrfTokenHeader;

    @Inject
    SessionService sessionService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        if (path.equals("/user/login") || path.equals("/user/register")) {
            return;
        }

        String csrfToken = requestContext.getHeaderString(csrfTokenHeader);
        if (csrfToken == null || !sessionService.validateCsrfToken(csrfToken)) {
            throw new WebApplicationException("Invalid CSRF Token", Response.Status.FORBIDDEN);
        }
    }
}
