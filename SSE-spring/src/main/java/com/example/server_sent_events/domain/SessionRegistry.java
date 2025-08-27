package com.example.server_sent_events.domain;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionRegistry {
    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void addEmitter(String sessionId, SseEmitter emitter) {
        emitters.put(sessionId, emitter);
    }

    public void removeEmitter(String sessionId) {
        emitters.remove(sessionId);
    }

    public SseEmitter getEmitter(String sessionId) {
        return emitters.get(sessionId);
    }

    public Collection<SseEmitter> getAllEmitters() {
        return emitters.values();
    }
}
