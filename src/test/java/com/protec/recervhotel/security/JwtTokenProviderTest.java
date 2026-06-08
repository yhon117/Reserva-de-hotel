package com.protec.recervhotel.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                "TestSecretKeyForJUnitTestsMustBeAtLeast256BitsLongForHS256!!",
                86400000L);
    }

    @Test
    void generateToken_ReturnsValidToken() {
        String token = jwtTokenProvider.generateToken("test@test.com", "ADMIN");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void getEmailFromToken_ReturnsCorrectEmail() {
        String token = jwtTokenProvider.generateToken("test@test.com", "ADMIN");

        String email = jwtTokenProvider.getEmailFromToken(token);

        assertEquals("test@test.com", email);
    }

    @Test
    void getRolFromToken_ReturnsCorrectRol() {
        String token = jwtTokenProvider.generateToken("test@test.com", "ADMIN");

        String rol = jwtTokenProvider.getRolFromToken(token);

        assertEquals("ADMIN", rol);
    }

    @Test
    void validateToken_WithValidToken_ReturnsTrue() {
        String token = jwtTokenProvider.generateToken("test@test.com", "USER");

        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_WithInvalidToken_ReturnsFalse() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    void validateToken_WithExpiredToken_ReturnsFalse() {
        JwtTokenProvider shortLived = new JwtTokenProvider(
                "TestSecretKeyForJUnitTestsMustBeAtLeast256BitsLongForHS256!!",
                1L);

        String token = shortLived.generateToken("test@test.com", "USER");

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertFalse(shortLived.validateToken(token));
    }
}
