package com.example.property_service.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private String secret = "mytestsecretmytestsecretmytestsecret"; // must be 32+ chars
    private String token;

    @BeforeEach
    void setup() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "jwtSecret", secret);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        token = Jwts.builder()
                .setSubject("testUser")
                .claim("email", "test@email.com")
                .claim("role", "ADMIN")
                .setIssuedAt(new Date())
                .signWith(key)
                .compact();
    }

    @Test
    void testExtractSubject() {
        String subject = jwtService.extractSubject(token);
        assertEquals("testUser", subject);
    }

    @Test
    void testExtractEmail() {
        String email = jwtService.extractEmail(token);
        assertEquals("test@email.com", email);
    }

    @Test
    void testExtractRole() {
        String role = jwtService.extractRole(token);
        assertEquals("ADMIN", role);
    }

    @Test
    void testTokenValid() {
        assertTrue(jwtService.isTokenValid(token));
    }
    @Test
    void testInvalidToken() {
        assertThrows(Exception.class, () -> {
            jwtService.isTokenValid("invalid.token.here");
        });
    }
}