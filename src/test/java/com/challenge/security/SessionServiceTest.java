package com.challenge.security;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionServiceTest {

    @InjectMocks
    private SessionService sessionService;

    @Mock
    private Logger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inicializa cualquier configuración adicional si es necesario
    }

    @Test
    void testGenerateJwtToken() {
        String username = "testUser";
        String token = sessionService.generateJwtToken(username);

        assertNotNull(token);
        assertTrue(token.startsWith("eyJ")); // Asegúrate de que el token comience con el encabezado JWT
    }

    @Test
    void testGenerateCsrfToken() {
        String token = sessionService.generateCsrfToken();

        assertNotNull(token);
        assertTrue(UUID.fromString(token) instanceof UUID); // Verifica que el token es un UUID válido
    }

    @Test
    void testValidateCsrfToken_Valid() {
        String token = sessionService.generateCsrfToken();
        boolean isValid = sessionService.validateCsrfToken(token);

        assertTrue(isValid);
    }


//    @Test
//    void testValidateJwtToken_Valid() {
//        String username = "testUser";
//        String token = sessionService.generateJwtToken(username);
//
//        // Mock del JsonWebToken para la validación
//        JsonWebToken jwt = mock(JsonWebToken.class);
//        when(jwt.getName()).thenReturn(username);
//
//        // Aquí tendrías que ajustar esto según la implementación de validación que uses
//        JsonWebToken validatedToken = sessionService.validateJwtToken(token);
//
//        assertNotNull(validatedToken);
//        assertEquals(username, validatedToken.getName());
//    }

    @Test
    void testValidateJwtToken_Invalid() {
        String invalidToken = "invalidToken";

        JsonWebToken validatedToken = sessionService.validateJwtToken(invalidToken);

        assertNull(validatedToken);
    }
}
