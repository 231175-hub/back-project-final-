package com.epiis.finalproject.helper;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderHelperTest {

    @Test
    void testPasswordEncoderCreationAndEncoding() {
        PasswordEncoderHelper helper = new PasswordEncoderHelper();
        BCryptPasswordEncoder encoder = helper.passwordEncoder();
        assertNotNull(encoder);

        String rawPassword = "mySecretPassword123";
        String encoded = encoder.encode(rawPassword);

        assertNotEquals(rawPassword, encoded);
        assertTrue(encoder.matches(rawPassword, encoded));
    }
}
