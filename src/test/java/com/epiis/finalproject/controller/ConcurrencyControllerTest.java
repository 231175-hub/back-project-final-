package com.epiis.finalproject.controller;

import com.epiis.finalproject.service.ConcurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class ConcurrencyControllerTest {

    private ConcurrencyService concurrencyService;
    private ConcurrencyController controller;

    @BeforeEach
    void setUp() {
        concurrencyService = mock(ConcurrencyService.class);
        controller = new ConcurrencyController(concurrencyService);
    }

    @Test
    void testHeartbeat() {
        assertDoesNotThrow(() -> controller.heartbeat("g1", "user1"));
        verify(concurrencyService, times(1)).registerHeartbeat("g1", "user1");
    }

    @Test
    void testDisconnect() {
        assertDoesNotThrow(() -> controller.disconnect("g1", "user1"));
        verify(concurrencyService, times(1)).removeUser("g1", "user1");
    }
}
