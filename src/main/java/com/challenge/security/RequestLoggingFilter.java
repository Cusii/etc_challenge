package com.challenge.security;

import io.quarkus.logging.Log;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class RequestLoggingFilter implements ContainerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Log all headers
        Log.info("Entro a leer Header");
        //requestContext.getHeaders().forEach((name, values) ->
        //        values.forEach(value -> LOG.info("Header {}: {}", name, value))
        //);
    }
}
