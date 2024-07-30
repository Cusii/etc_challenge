package com.challenge.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class SessionService {

    private static final Logger LOG = LoggerFactory.getLogger(SessionService.class);
    private final Map<String, TokenData> csrfTokens = new HashMap<>();

    private static final String SECRET_KEY = "l9r7mcywzWO5Drc8hU0MSLMb3rMuzbUvcI2KTWb+cxQ=";
    private static final long CSRF_TOKEN_EXPIRATION_MS = 15 * 60 * 1000; // 15 minutos

    public String generateJwtToken(String username) {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "HmacSHA256");

        String token = Jwt.issuer("http://localhost:8080")
                .subject(username)
                .expiresIn(3600) // 1 hora
                .sign(secretKeySpec); // Firma el JWT con la clave secreta configurada

        LOG.info("Generated JWT for user: {}", username);
        return token;
    }

    public JsonWebToken validateJwtToken(String token) {
        return null;
    }

    public String generateCsrfToken() {
        String token = UUID.randomUUID().toString();
        csrfTokens.put(token, new TokenData(token, System.currentTimeMillis()));
        return token;
    }

    public boolean validateCsrfToken(String token) {
        TokenData tokenData = csrfTokens.get(token);
        if (tokenData == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - tokenData.timestamp > CSRF_TOKEN_EXPIRATION_MS) {
            csrfTokens.remove(token);
            return false;
        }
        return true;
    }

    private static class TokenData {
        String token;
        long timestamp;

        TokenData(String token, long timestamp) {
            this.token = token;
            this.timestamp = timestamp;
        }
    }
}
