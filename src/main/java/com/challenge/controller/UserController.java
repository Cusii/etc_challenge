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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/user")
public class UserController {

    @Inject
    UserService userService;
    @Inject
    SessionService sessionService;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userLogin(UserLoginDTO userLoginDTO) {
        log.info("Attempting to login user: {}", userLoginDTO.getUserName());
        boolean isValid = userService.validateUserPassword(userLoginDTO.getUserName(), userLoginDTO.getPassword());

        if (isValid) {
            String jwtToken = sessionService.generateJwtToken(userLoginDTO.getUserName());
            String csrfToken = sessionService.generateCsrfToken();

            LoginResponse loginResponse = new LoginResponse(jwtToken, csrfToken);

            return Response.ok(loginResponse).header("X-CSRF-Token", csrfToken).build();
        } else {
            log.warn("Unauthorized login attempt for user: {}", userLoginDTO.getUserName(), "do not match");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userRegister(UserRegisterDTO userRegisterDTO) {
        log.info("Attempting to register user: {}", userRegisterDTO.getUserName());
        try {
            Users user =userService.userRegister(userRegisterDTO);

            String jwtToken = sessionService.generateJwtToken(user.getUserName());
            String csrfToken = sessionService.generateCsrfToken();

            LoginResponse loginResponse = new LoginResponse(jwtToken, csrfToken);

            return Response.ok(loginResponse).header("X-CSRF-Token", csrfToken).build();
        } catch (UserAlreadyExistsException e) {
            log.warn("User registration failed: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\":\"El usuario ya existe.\"}")
                    .build();
        }
    }
}