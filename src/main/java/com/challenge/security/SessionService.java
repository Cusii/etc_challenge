package com.challenge.security;

import com.challenge.model.TokenData;
import io.smallrye.jwt.build.Jwt;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.enterprise.context.ApplicationScoped;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ApplicationScoped
public class SessionService {

    private final Map<String, TokenData> csrfTokens = new ConcurrentHashMap<>();
    private static final String SECRET_KEY = "l9r7mcywzWO5Drc8hU0MSLMb3rMuzbUvcI2KTWb+cxQ=";
    private static final long CSRF_TOKEN_EXPIRATION_MS = 15 * 60 * 1000; // 15 minutos

    public String generateJwtToken(String username) {
        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "HmacSHA256");

        String token = Jwt.issuer("http://localhost:8080")
                .subject(username)
                .expiresIn(3600) // 1 hora
                .sign(secretKeySpec); // Firma el JWT con la clave secreta configurada

        log.info("Generated JWT: {}", token);
        return token;
    }

    public JsonWebToken validateJwtToken(String token) {
        return null;
    }

    public String generateCsrfToken() {
        String token = UUID.randomUUID().toString();
        csrfTokens.put(token, new TokenData(token, System.currentTimeMillis()));
        log.info("Generated CSRF Token: {}", token);
        return token;
    }

    public boolean validateCsrfToken(String token) {
        TokenData tokenData = csrfTokens.get(token);
        if (tokenData == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - tokenData.getTimestamp() > CSRF_TOKEN_EXPIRATION_MS) {
            csrfTokens.remove(token);
            log.warn("CSRF Token expired: {}", token);
            return false;
        }
        return true;
    }
}
