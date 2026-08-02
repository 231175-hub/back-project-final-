package com.epiis.finalproject.config;

import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.entity.EntityUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "secretkey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604800000L);
    }

    @Test
    void testGenerateAccessTokenAndExtractClaims() {
        EntityUser user = new EntityUser();
        user.setIdUser("u123");
        user.setEmail("test@domain.com");

        EntityRole role = new EntityRole();
        role.setNameRole("ADMIN");
        user.setParentRole(role);

        String token = jwtService.generateAccessToken(user);
        assertNotNull(token);
        assertEquals("test@domain.com", jwtService.extractUsername(token));
        assertEquals("u123", jwtService.extractUserId(token));
        assertEquals("ADMIN", jwtService.extractRole(token));
        assertTrue(jwtService.isTokenValid(token));
        assertTrue(jwtService.isTokenValid(token, "test@domain.com"));
    }

    @Test
    void testGenerateRefreshToken() {
        EntityUser user = new EntityUser();
        user.setIdUser("u123");
        user.setEmail("test@domain.com");

        String refreshToken = jwtService.generateRefreshToken(user);
        assertNotNull(refreshToken);
        assertTrue(jwtService.isTokenValid(refreshToken));
    }
}
