package com.epiis.finalproject.controller;

import com.epiis.finalproject.config.JwtService;
import com.epiis.finalproject.dto.request.auth.RequestLogin;
import com.epiis.finalproject.dto.request.auth.RequestRefreshToken;
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

    @Test
    void testRefreshInvalidToken() {
        RequestRefreshToken req = new RequestRefreshToken();
        req.setRefreshToken("invalidToken");
        when(jwtService.isTokenValid("invalidToken")).thenReturn(false);

        ResponseEntity<ResponseAuth> response = authController.refresh(req);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testRefreshSuccess() {
        RequestRefreshToken req = new RequestRefreshToken();
        req.setRefreshToken("validToken");
        when(jwtService.isTokenValid("validToken")).thenReturn(true);
        when(jwtService.extractUsername("validToken")).thenReturn("user@test.com");

        EntityUser user = new EntityUser();
        user.setEmail("user@test.com");
        user.setRefreshToken("validToken");
        when(repositoryUser.findFirstByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("newAccess");
        when(jwtService.generateRefreshToken(user)).thenReturn("newRefresh");

        ResponseEntity<ResponseAuth> response = authController.refresh(req);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("newAccess", response.getBody().getAccessToken());
    }

    @Test
    void testForgotPassword() {
        com.epiis.finalproject.dto.request.auth.RequestForgotPassword req = new com.epiis.finalproject.dto.request.auth.RequestForgotPassword();
        req.setEmail("user@test.com");

        EntityUser user = new EntityUser();
        user.setEmail("user@test.com");
        when(repositoryUser.findFirstByEmail("user@test.com")).thenReturn(Optional.of(user));

        ResponseEntity<java.util.Map<String, Object>> response = authController.forgotPassword(req);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("success"));
        verify(emailService, times(1)).sendResetPasswordEmail(eq("user@test.com"), anyString());
    }

    @Test
    void testResetPasswordInvalidToken() {
        com.epiis.finalproject.dto.request.auth.RequestResetPassword req = new com.epiis.finalproject.dto.request.auth.RequestResetPassword();
        req.setToken("badToken");
        req.setPassword("newPass");

        when(repositoryUser.findByResetPasswordToken("badToken")).thenReturn(Optional.empty());

        ResponseEntity<java.util.Map<String, Object>> response = authController.resetPassword(req);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("success"));
    }

    @Test
    void testResetPasswordSuccess() {
        com.epiis.finalproject.dto.request.auth.RequestResetPassword req = new com.epiis.finalproject.dto.request.auth.RequestResetPassword();
        req.setToken("goodToken");
        req.setPassword("newPass");

        EntityUser user = new EntityUser();
        user.setResetPasswordToken("goodToken");
        user.setResetPasswordTokenExpiry(new java.util.Date(System.currentTimeMillis() + 60000));
        when(repositoryUser.findByResetPasswordToken("goodToken")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        ResponseEntity<java.util.Map<String, Object>> response = authController.resetPassword(req);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("success"));
        verify(repositoryUser, times(1)).save(user);
    }
}
