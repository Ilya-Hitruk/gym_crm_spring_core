package com.hitruk.gym.workload.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET =
            Base64.getEncoder().encodeToString("test-secret-key-that-is-long-enough-for-hmac-sha256".getBytes());

    private JwtService jwtService;
    private SecretKey key;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET);
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));
    }

    @Test
    void isTokenValid_validToken_returnsTrue() {
        String token = Jwts.builder()
                .subject("gym-crm-service")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(key)
                .compact();

        assertTrue(jwtService.isTokenValid(token));
        assertEquals("gym-crm-service", jwtService.extractSubject(token));
    }

    @Test
    void isTokenValid_expiredToken_returnsFalse() {
        String token = Jwts.builder()
                .subject("gym-crm-service")
                .issuedAt(Date.from(Instant.now().minusSeconds(7200)))
                .expiration(Date.from(Instant.now().minusSeconds(3600)))
                .signWith(key)
                .compact();

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_wrongSecret_returnsFalse() {
        String otherSecret =
                Base64.getEncoder().encodeToString("another-completely-different-secret-key-256-bits".getBytes());
        SecretKey otherKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(otherSecret));

        String token = Jwts.builder()
                .subject("gym-crm-service")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(otherKey)
                .compact();

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_garbageToken_returnsFalse() {
        assertFalse(jwtService.isTokenValid("not-a-jwt-token"));
    }
}
