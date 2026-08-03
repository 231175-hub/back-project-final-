package com.epiis.finalproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmailServiceTest {

    private static final String RESET_URL = "http://localhost:4200/reset-password?token=token123";

    private EmailService emailService;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        emailService = new EmailService();
        restTemplate = mock(RestTemplate.class);
        ReflectionTestUtils.setField(emailService, "resendApiKey", "test-key");
        ReflectionTestUtils.setField(emailService, "restTemplate", restTemplate);
    }

    @Test
    void testSendResetPasswordEmailWithoutApiKey() {
        ReflectionTestUtils.setField(emailService, "resendApiKey", "");
        assertDoesNotThrow(() -> emailService.sendResetPasswordEmailSync("user@example.com", RESET_URL));
    }

    @Test
    void testSendResetPasswordEmailSuccess() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));
        assertDoesNotThrow(() -> emailService.sendResetPasswordEmailSync("user@example.com", RESET_URL));
    }

    @Test
    void testSendResetPasswordEmailServerError() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("err", HttpStatus.BAD_REQUEST));
        assertDoesNotThrow(() -> emailService.sendResetPasswordEmailSync("user@example.com", RESET_URL));
    }

    @Test
    void testSendResetPasswordEmailException() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("boom"));
        assertDoesNotThrow(() -> emailService.sendResetPasswordEmailSync("user@example.com", RESET_URL));
    }
}
