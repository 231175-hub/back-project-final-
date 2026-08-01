package com.epiis.finalproject.controller;

import com.epiis.finalproject.config.JwtService;
import com.epiis.finalproject.dto.request.auth.RequestLogin;
import com.epiis.finalproject.dto.response.auth.ResponseAuth;
import com.epiis.finalproject.entity.EntityRole;
import com.epiis.finalproject.entity.EntityUser;
import com.epiis.finalproject.repository.RepositoryUser;
import com.epiis.finalproject.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private RepositoryUser repositoryUser;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private EmailService emailService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        repositoryUser = mock(RepositoryUser.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        emailService = mock(EmailService.class);

        authController = new AuthController(repositoryUser, passwordEncoder, jwtService, emailService);
    }

    @Test
    void testLoginUserNotFound() {
        when(repositoryUser.findFirstByEmail("notfound@test.com")).thenReturn(Optional.empty());

        RequestLogin req = new RequestLogin();
        req.setEmail("notfound@test.com");
        req.setPassword("pass");

        ResponseEntity<ResponseAuth> response = authController.login(req);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testLoginPasswordIncorrect() {
        EntityUser user = new EntityUser();
        user.setEmail("user@test.com");
        user.setPassword("encoded");
        when(repositoryUser.findFirstByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encoded")).thenReturn(false);

        RequestLogin req = new RequestLogin();
        req.setEmail("user@test.com");
        req.setPassword("wrongpass");

        ResponseEntity<ResponseAuth> response = authController.login(req);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testLoginSuccess() {
        EntityUser user = new EntityUser();
        user.setIdUser("u1");
        user.setEmail("user@test.com");
        user.setPassword("encoded");
        user.setFirstName("Juan");
        user.setSurName("Perez");

        EntityRole role = new EntityRole();
        role.setNameRole("ADMIN");
        user.setParentRole(role);

        when(repositoryUser.findFirstByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correctpass", "encoded")).thenReturn(true);
        when(jwtService.generateAccessToken(any(EntityUser.class))).thenReturn("accessToken123");
        when(jwtService.generateRefreshToken(any(EntityUser.class))).thenReturn("refreshToken123");

        RequestLogin req = new RequestLogin();
        req.setEmail("user@test.com");
        req.setPassword("correctpass");

        ResponseEntity<ResponseAuth> response = authController.login(req);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("accessToken123", response.getBody().getAccessToken());
    }
}
