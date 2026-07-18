package com.epiis.finalproject.controller;

import com.epiis.finalproject.service.ConcurrencyService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/intranet/concurrency")
public class ConcurrencyController {

    private final ConcurrencyService concurrencyService;
    private final Map<String, ScheduledExecutorService> emittersSchedulers = new ConcurrentHashMap<>();

    public ConcurrencyController(ConcurrencyService concurrencyService) {
        this.concurrencyService = concurrencyService;
    }

    @PostMapping("/heartbeat/{idGroup}/{username}")
    public void heartbeat(@PathVariable String idGroup, @PathVariable String username) {
        concurrencyService.registerHeartbeat(idGroup, username);
    }

    @PostMapping("/disconnect/{idGroup}/{username}")
    public void disconnect(@PathVariable String idGroup, @PathVariable String username) {
        concurrencyService.removeUser(idGroup, username);
    }

    @GetMapping(value = "/active-session/{idGroup}/{username}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String idGroup, @PathVariable String username) {
        SseEmitter emitter = new SseEmitter(300000L); // 5 minutes timeout
        
        concurrencyService.registerHeartbeat(idGroup, username);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        String schedulerKey = idGroup + "-" + username + "-" + System.currentTimeMillis();
        emittersSchedulers.put(schedulerKey, scheduler);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<String> otherEditors = concurrencyService.getActiveEditors(idGroup, username);
                emitter.send(SseEmitter.event()
                        .name("editors")
                        .data(otherEditors));
            } catch (IOException e) {
                // Client disconnected or completed
                emitter.complete();
                scheduler.shutdown();
                emittersSchedulers.remove(schedulerKey);
                concurrencyService.removeUser(idGroup, username);
            }
        }, 0, 3, TimeUnit.SECONDS);

        emitter.onCompletion(() -> {
            scheduler.shutdown();
            emittersSchedulers.remove(schedulerKey);
            concurrencyService.removeUser(idGroup, username);
        });

        emitter.onTimeout(() -> {
            emitter.complete();
            scheduler.shutdown();
            emittersSchedulers.remove(schedulerKey);
            concurrencyService.removeUser(idGroup, username);
        });

        return emitter;
    }
}
