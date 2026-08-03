package com.epiis.finalproject.controller;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Base class that centralises repeated assertion helpers used across
 * all controller unit tests, eliminating SonarQube-detected duplications.
 */
public abstract class BaseControllerTest {

    /**
     * Asserts that a ResponseEntity returns HTTP 200 and is not null.
     */
    protected void assertOk(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    /**
     * Creates an empty HashMap to use as a mock return value for getAll/getById methods.
     */
    protected Map<String, Object> emptyMap() {
        return new HashMap<>();
    }
}
