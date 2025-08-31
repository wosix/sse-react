package com.example.server_sent_events.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static com.example.server_sent_events.domain.Constants.SSE_EMITTER_ATTRIBUTE;

@Service
public class NotificationService {

    public SseEmitter registerSession(HttpSession session) {

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

    public ResponseEntity<String> sendNotification(HttpSession session, String type, String message) {

        String sessionId = session.getId();
        SseEmitter emitter = (SseEmitter) session.getAttribute(SSE_EMITTER_ATTRIBUTE);

        if (emitter == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not connected to SSE");
        }

        try {
            emitter.send(newMessage(type, message));
            System.out.println("SSE message send to session: " + sessionId);

            return ResponseEntity.ok("Notification sent");
        } catch (IOException e) {
            session.removeAttribute(SSE_EMITTER_ATTRIBUTE);
            System.out.println("SSE error on sending message to session: " + sessionId);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending notification: " + e.getMessage());
        }
    }

    private String newMessage(String type, String message) {
        return "{" +
                "\"type\":\"" + type + "\"," +
                "\"message\":\"" + message + "\"" +
                "}";
    }

}
