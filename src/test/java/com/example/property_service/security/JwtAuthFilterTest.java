package com.example.property_service.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    private FilterChain filterChain;

    @BeforeEach
    void setup() {
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testNoAuthorizationHeader() throws Exception {
        JwtService jwtService = new JwtService();
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testValidJwtAuthentication() throws Exception {
        Claims claims = io.jsonwebtoken.Jwts.claims();
        claims.setSubject("user1");
        claims.put("role", "ADMIN");

        JwtService jwtService = new JwtService() {
            @Override
            public Claims parseClaims(String token) {
                return claims;
            }
        };

        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer validtoken");

        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testInvalidTokenClearsContext() throws Exception {
        JwtService jwtService = new JwtService() {
            @Override
            public Claims parseClaims(String token) {
                throw new RuntimeException("Invalid token");
            }
        };

        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer badtoken");

        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}