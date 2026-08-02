package com.epiis.finalproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EmailServiceTest {

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService();
    }

    @Test
    void testSendResetPasswordEmailWithoutApiKey() {
        assertDoesNotThrow(() -> emailService.sendResetPasswordEmail("user@example.com", "token123"));
    }
}
