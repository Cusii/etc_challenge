package com.challenge.controller;

import com.challenge.entity.UserEntity;
import com.challenge.exception.UserAlreadyExistsException;
import com.challenge.model.LoginResponse;
import com.challenge.model.UserLoginDTO;
import com.challenge.model.UserRegisterDTO;
import com.challenge.security.SessionService;
import com.challenge.service.UserService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUserLogin_Success() {
        UserLoginDTO userLoginDTO = new UserLoginDTO("username", "password");
        when(userService.validateUserPassword("username", "password")).thenReturn(true);
        when(sessionService.generateJwtToken("username")).thenReturn("jwtToken");
        when(sessionService.generateCsrfToken()).thenReturn("csrfToken");

        Response response = userController.userLogin(userLoginDTO);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        LoginResponse loginResponse = (LoginResponse) response.getEntity();
        assertEquals("jwtToken", loginResponse.getJwtToken());
        assertEquals("csrfToken", loginResponse.getCsrfToken());
        assertEquals("csrfToken", response.getHeaderString("X-CSRF-Token"));
    }

    @Test
    void testUserLogin_Failure() {
        UserLoginDTO userLoginDTO = new UserLoginDTO("username", "wrongPassword");
        when(userService.validateUserPassword("username", "wrongPassword")).thenReturn(false);

        Response response = userController.userLogin(userLoginDTO);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testUserRegister_Success() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("username", "password", "12", "Male", "M");
        UserEntity user = new UserEntity();
        user.setUserName("username");
        when(userService.userRegister(userRegisterDTO)).thenReturn(user);
        when(sessionService.generateJwtToken("username")).thenReturn("jwtToken");
        when(sessionService.generateCsrfToken()).thenReturn("csrfToken");

        Response response = userController.userRegister(userRegisterDTO);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        LoginResponse loginResponse = (LoginResponse) response.getEntity();
        assertEquals("jwtToken", loginResponse.getJwtToken());
        assertEquals("csrfToken", loginResponse.getCsrfToken());
        assertEquals("csrfToken", response.getHeaderString("X-CSRF-Token"));
    }

    @Test
    void testUserRegister_UserAlreadyExists() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("username", "password", "32", "male", "M");
        when(userService.userRegister(userRegisterDTO)).thenThrow(new UserAlreadyExistsException("El usuario ya existe."));

        Response response = userController.userRegister(userRegisterDTO);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        String errorMessage = (String) response.getEntity();
        assertTrue(errorMessage.contains("El usuario ya existe."));
    }
}
