package com.example.server_sent_events.controllers;

import com.example.server_sent_events.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.server_sent_events.domain.Constants.NOTIFICATION_TYPE_INFO;
import static com.example.server_sent_events.domain.Constants.getCookiesAsString;

@RestController
public class ActionController {

    private final NotificationService notificationService;

    @Autowired
    public ActionController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/action")
    public ResponseEntity<String> action(HttpServletRequest request) {
        System.out.println("/action SESSION ID: " + request.getSession().getId());
        System.out.println("/action cookies JSESSIONID: " + getCookiesAsString(request.getCookies()));

//        HttpSession session = request.getSession(false);

        HttpSession session = request.getSession();

        System.out.println("Session isNew: " + session.isNew());

        if (session.isNew()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No active session");
        }

        return notificationService.sendNotification(session, NOTIFICATION_TYPE_INFO, "action message");
    }

}




