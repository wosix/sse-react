package com.example.server_sent_events.controllers;

import com.example.server_sent_events.domain.SessionRegistry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.Date;

@RestController
public class SSEController {

    private static final String SSE_EMITTER_ATTRIBUTE = "sseEmitter";

    @Autowired
    private SessionRegistry sessionRegistry;

    @GetMapping("/events")
    public SseEmitter streamSSE(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String sessionId = session.getId();

        SseEmitter existingEmitter = (SseEmitter) session.getAttribute(SSE_EMITTER_ATTRIBUTE);
        if (existingEmitter != null) {
            existingEmitter.complete();
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

        sessionRegistry.addEmitter(sessionId, emitter);

        try {
            emitter.send(newMessage("success", "connected succesful message"));
            System.out.println("SSE connection message send to session: " + sessionId);
        } catch (IOException e) {
            session.removeAttribute(SSE_EMITTER_ATTRIBUTE);
            sessionRegistry.removeEmitter(sessionId);
            System.out.println("SSE connection error for session: " + sessionId);
        }


        System.out.println("=== /events CONNECTION ===");
        System.out.println("NEW SESSION ID: " + sessionId);
        System.out.println("SESSION CREATION TIME: " + new Date(session.getCreationTime()));

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                System.out.println("COOKIE: " + cookie.getName() + " = " + cookie.getValue());
            }
        }

        return emitter;
    }

    @PostMapping("/action")
    public ResponseEntity<String> action(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
        }

        String sessionId = session.getId();
        SseEmitter emitter = sessionRegistry.getEmitter(sessionId);

        if (emitter == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not connected to SSE");
        }

        try {
            emitter.send(newMessage("info", "message"));
            System.out.println("SSE message send to session: " + sessionId);

            return ResponseEntity.ok("Notification sent");
        } catch (IOException e) {
            session.removeAttribute(SSE_EMITTER_ATTRIBUTE);
            sessionRegistry.removeEmitter(sessionId);
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
