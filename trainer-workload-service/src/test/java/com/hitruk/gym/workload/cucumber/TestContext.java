package com.hitruk.gym.workload.cucumber;

import io.cucumber.spring.ScenarioScope;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
@ScenarioScope
public class TestContext {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private ResponseEntity<String> lastResponse;

    public String baseUrl() {
        return "http://localhost:" + port;
    }

    public TestRestTemplate restTemplate() {
        return restTemplate;
    }

    public void setLastResponse(ResponseEntity<String> response) {
        this.lastResponse = response;
    }

    public ResponseEntity<String> lastResponse() {
        return lastResponse;
    }

    public String generateTestToken() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        Instant now = Instant.now();
        return Jwts.builder()
                .subject("component-test-client")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(key)
                .compact();
    }
}
