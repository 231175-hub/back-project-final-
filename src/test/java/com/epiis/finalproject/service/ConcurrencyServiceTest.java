package com.epiis.finalproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyServiceTest {

    private ConcurrencyService concurrencyService;

    @BeforeEach
    void setUp() {
        concurrencyService = new ConcurrencyService();
    }

    @Test
    void testRegisterHeartbeatAndGetActiveEditors() {
        concurrencyService.registerHeartbeat("group1", "user1");
        concurrencyService.registerHeartbeat("group1", "user2");

        List<String> activeUser1 = concurrencyService.getActiveEditors("group1", "user1");
        assertEquals(1, activeUser1.size());
        assertTrue(activeUser1.contains("user2"));

        List<String> activeUser2 = concurrencyService.getActiveEditors("group1", "user2");
        assertEquals(1, activeUser2.size());
        assertTrue(activeUser2.contains("user1"));
    }

    @Test
    void testGetActiveEditorsWhenNoEditorsExist() {
        List<String> active = concurrencyService.getActiveEditors("nonexistent", "user1");
        assertTrue(active.isEmpty());
    }

    @Test
    void testRemoveUser() {
        concurrencyService.registerHeartbeat("group1", "user1");
        concurrencyService.registerHeartbeat("group1", "user2");

        concurrencyService.removeUser("group1", "user2");

        List<String> activeUser1 = concurrencyService.getActiveEditors("group1", "user1");
        assertTrue(activeUser1.isEmpty());
    }

    @Test
    void testRemoveUserNonExistentGroup() {
        assertDoesNotThrow(() -> concurrencyService.removeUser("group999", "user1"));
    }
}
