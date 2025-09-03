package com.example.server_sent_events.service;

import com.example.server_sent_events.domain.Notification;
import com.example.server_sent_events.domain.NotificationManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.example.server_sent_events.domain.Constants.SSE_EMITTER_ATTRIBUTE;

@Service
public class NotificationService {

    private final NotificationManager notificationManager;

    public NotificationService(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public SseEmitter register(HttpSession session) {
        String sessionId = session.getId();

        SseEmitter existingEmitter = (SseEmitter) session.getAttribute(SSE_EMITTER_ATTRIBUTE);
        if (existingEmitter != null) {
            return existingEmitter;
        }

        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        session.setAttribute(SSE_EMITTER_ATTRIBUTE, emitter);

        emitter.onCompletion(() -> {
            session.removeAttribute(SSE_EMITTER_ATTRIBUTE);
            System.out.println("SSE completed for session: " + sessionId);
        });

        emitter.onTimeout(() -> {
            session.removeAttribute(SSE_EMITTER_ATTRIBUTE);
            System.out.println("SSE timeout for session: " + sessionId);
        });

        emitter.onError((e) -> {
            session.removeAttribute(SSE_EMITTER_ATTRIBUTE);
            System.out.println("SSE error for session: " + sessionId + ": " + e.getMessage());
        });

        return emitter;
    }

    public ResponseEntity<String> queueNotification(HttpSession session, Notification notification) {
        String sessionId = session.getId();
        SseEmitter emitter = (SseEmitter) session.getAttribute(SSE_EMITTER_ATTRIBUTE);

        if (emitter == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not connected to SSE");
        }

        notificationManager.addNotification(sessionId, notification);

        return ResponseEntity.ok("Notification registered");
    }

}
