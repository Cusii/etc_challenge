package com.challenge.controller;

import com.challenge.entity.UserEntity;
import com.challenge.exception.UserAlreadyExistsException;
import com.challenge.model.*;
import com.challenge.security.SessionService;
import com.challenge.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    UserService userService;
    @Inject
    SessionService sessionService;

    @POST
    @Path("/login")
    public Response userLogin(UserLoginDTO userLoginDTO) {
        log.info("Attempting to login user: {}", userLoginDTO.getUserName());
        boolean isValid = userService.validateUserPassword(userLoginDTO.getUserName(), userLoginDTO.getPassword());

        if (isValid) {
            String jwtToken = sessionService.generateJwtToken(userLoginDTO.getUserName());
            String csrfToken = sessionService.generateCsrfToken();

            LoginResponse loginResponse = new LoginResponse(jwtToken, csrfToken);

            return Response.ok(loginResponse).header("X-CSRF-Token", csrfToken).build();
        } else {
            log.warn("Unauthorized login for user: {}", userLoginDTO.getUserName());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Path("/register")
    public Response userRegister(UserRegisterDTO userRegisterDTO) {
        log.info("Attempting to register user: {}", userRegisterDTO.getUserName());
        try {
            UserEntity user = userService.userRegister(userRegisterDTO);
            log.debug("User created with ID: {}", user.getUserId());

            String jwtToken = sessionService.generateJwtToken(user.getUserName());
            String csrfToken = sessionService.generateCsrfToken();

            LoginResponse loginResponse = new LoginResponse(jwtToken, csrfToken);
            return Response.ok(loginResponse).header("X-CSRF-Token", csrfToken).build();
        } catch (UserAlreadyExistsException e) {
            log.warn("User Register failed: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        log.info("Attempting to get User with ID {}", id);
        try {
            UserResponseDTO user = userService.getUserById(id);
            return Response.ok(user).build();
        } catch (Exception e) {
            log.error("Error retrieving task: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error al leer el usuario.")).build();
        }
    }
}