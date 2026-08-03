package com.epiis.finalproject.controller;

import com.epiis.finalproject.service.ConcurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Test
    void testSubscribeReturnsEmitter() {
        when(concurrencyService.getActiveEditors("g1", "user1")).thenReturn(List.of("editor1"));

        SseEmitter emitter = controller.subscribe("g1", "user1");

        assertNotNull(emitter);
        verify(concurrencyService, times(1)).registerHeartbeat("g1", "user1");
    }
}
