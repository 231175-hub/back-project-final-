package com.epiis.finalproject.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConcurrencyService {
    // Map from idGroup to Map of username -> lastActiveTimestamp
    private final Map<String, Map<String, Long>> groupEditors = new ConcurrentHashMap<>();
    private static final long SESSION_TIMEOUT_MS = 8000; // 8 seconds timeout

    public void registerHeartbeat(String idGroup, String username) {
        groupEditors.computeIfAbsent(idGroup, k -> new ConcurrentHashMap<>())
                    .put(username, System.currentTimeMillis());
    }

    public void removeUser(String idGroup, String username) {
        Map<String, Long> editors = groupEditors.get(idGroup);
        if (editors != null) {
            editors.remove(username);
        }
    }

    public List<String> getActiveEditors(String idGroup, String currentUsername) {
        Map<String, Long> editors = groupEditors.get(idGroup);
        if (editors == null) {
            return Collections.emptyList();
        }

        long now = System.currentTimeMillis();
        List<String> activeList = new ArrayList<>();

        editors.forEach((username, lastActive) -> {
            if (now - lastActive < SESSION_TIMEOUT_MS) {
                if (!username.equals(currentUsername)) {
                    activeList.add(username);
                }
            } else {
                // Eagerly remove expired session
                editors.remove(username);
            }
        });

        return activeList;
    }
}
