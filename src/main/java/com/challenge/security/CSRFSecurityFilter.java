package com.challenge.security;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class CSRFSecurityFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(CSRFSecurityFilter.class);

    @ConfigProperty(name = "quarkus.http.csrf.token.header")
    String csrfTokenHeader;

    @Inject
    SessionService sessionService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (requestContext.getUriInfo().getPath().contains("/user/login") || requestContext.getUriInfo().getPath().contains("/user/register")) {
            return;
        }

        String csrfToken = requestContext.getHeaderString(csrfTokenHeader);
        if (csrfToken == null || !sessionService.validateCsrfToken(csrfToken)) {
            throw new WebApplicationException("Invalid CSRF Token", Response.Status.FORBIDDEN);
        }
    }
}
