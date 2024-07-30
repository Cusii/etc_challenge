package com.challenge.controller;

import com.challenge.entity.Users;
import com.challenge.exception.UserAlreadyExistsException;
import com.challenge.model.LoginResponse;
import com.challenge.model.UserLoginDTO;
import com.challenge.model.UserRegisterDTO;
import com.challenge.security.SessionService;
import com.challenge.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Inject
    UserService userService;

    @Inject
    SessionService sessionService;


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userLogin(UserLoginDTO userLoginDTO) {
        LOG.info("Attempting to log in user: {}", userLoginDTO.getUserName());
        boolean isValid = userService.validateUserPassword(userLoginDTO.getUserName(), userLoginDTO.getPassword());

        if (isValid) {
            String jwtToken = sessionService.generateJwtToken(userLoginDTO.getUserName());
            String csrfToken = sessionService.generateCsrfToken();

            LoginResponse loginResponse = new LoginResponse(jwtToken, csrfToken);

            return Response.ok(loginResponse).header("X-CSRF-Token", csrfToken).build();
        } else {
            LOG.warn("Unauthorized login attempt for user: {}", userLoginDTO.getUserName());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userRegister(UserRegisterDTO userRegisterDTO) {
        LOG.info("Attempting to register user: {}", userRegisterDTO.getUserName());
        try {
            Users user =userService.userRegister(userRegisterDTO);

            String jwtToken = sessionService.generateJwtToken(user.getUserName());
            String csrfToken = sessionService.generateCsrfToken();

            LoginResponse loginResponse = new LoginResponse(jwtToken, csrfToken);

            return Response.ok(loginResponse).header("X-CSRF-Token", csrfToken).build();
        } catch (UserAlreadyExistsException e) {
            LOG.warn("User registration failed: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\":\"Username already in use.\"}")
                    .build();
        }
    }
}