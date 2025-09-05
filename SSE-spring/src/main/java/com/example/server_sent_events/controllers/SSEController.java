package com.example.server_sent_events.controllers;

import com.example.server_sent_events.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.example.server_sent_events.domain.Constants.getCookiesAsString;

@RestController
public class SSEController {

    private final NotificationService notificationService;

    @Autowired
    public SSEController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/events")
    public SseEmitter streamSSE(HttpServletRequest request) {
        System.out.println("/events - " + getCookiesAsString(request.getCookies()));

        HttpSession session = request.getSession();
        return notificationService.registerSession(session);
    }

}
