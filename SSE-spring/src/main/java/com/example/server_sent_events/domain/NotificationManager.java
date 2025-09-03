package com.example.server_sent_events.domain;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.server_sent_events.domain.Constants.SSE_EMITTER_ATTRIBUTE;

@Component
public class NotificationManager {

    private final HttpSessionRegistry sessionRegistry;
    private final ConcurrentHashMap<String, List<Notification>> pendingNotifications;
    private final ScheduledExecutorService scheduler;

    public NotificationManager(HttpSessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
        pendingNotifications = new ConcurrentHashMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

    }

    public void addNotification(String sessionId, Notification notification) {
        pendingNotifications.compute(sessionId, (key, notifications) -> {
            if (notifications == null) {
                notifications = new CopyOnWriteArrayList<>();
            }
            notifications.add(notification);
            System.out.println("Registered message: " + sessionId + " - " + notification.getMessageJson());
            return notifications;
        });
    }

    @PostConstruct
    private void startNotificationProcessor() {
        scheduler.scheduleAtFixedRate(() -> {
            processPendingNotifications();
            showSessionsLastAccessTimes();
        }, 10, 5, TimeUnit.SECONDS);
    }

    private void showSessionsLastAccessTimes() {
        HttpSessionRegistry.printAllLastAccessedTime();
    }


    private void processPendingNotifications() {
        AtomicInteger count = new AtomicInteger();
        pendingNotifications.forEach((sessionId, notifications) -> {
            if (!notifications.isEmpty()) {
                sendNotificationsForSession(sessionId);
                count.getAndIncrement();
            }
            System.out.println("Sending " + getPendingCount(sessionId) + " messages to sessionId " + sessionId);
        });
        System.out.println("Sent mesages to " + count + " sessions");
    }

    private void sendNotificationsForSession(String sessionId) {
        List<Notification> notifications = pendingNotifications.get(sessionId);

        if (!HttpSessionRegistry.sessionExists(sessionId)) {
            pendingNotifications.remove(sessionId);
            System.out.println(sessionId + " does not exists in register");
            return;
        }

        notifications.forEach(notification -> {
            sendNotification(sessionId, notification);
        });

        pendingNotifications.remove(sessionId);
    }

    private void sendNotification(String sessionId, Notification notification) {
        HttpSession session = HttpSessionRegistry.find(sessionId);
        if (session != null) {
            SseEmitter emitter = (SseEmitter) session.getAttribute(SSE_EMITTER_ATTRIBUTE);
            if (emitter != null) {
                try {
                    emitter.send(notification.getMessageJson());

                    System.out.println("SENT: " + sessionId + " - " + notification.getMessageJson());
                } catch (Exception e) {
                    System.err.println("Failed to send to session " + sessionId + ": " + e.getMessage());
                }
            }
        }
    }

//    public void triggerSend(String sessionId) {
//        sendNotificationsForSession(sessionId);
//    }
//
//    public void triggerSendForAll() {
//        pendingNotifications.keySet().forEach(this::sendNotificationsForSession);
//    }
//
//    public boolean hasNotifications(String sessionId) {
//        List<Notification> notifications = pendingNotifications.get(sessionId);
//        return notifications != null && !notifications.isEmpty();
//    }

    public int getPendingCount(String sessionId) {
        List<Notification> notifications = pendingNotifications.get(sessionId);
        return notifications != null ? notifications.size() : 0;
    }

    @PreDestroy
    public void cleanup() {
        scheduler.shutdown();
        pendingNotifications.clear();
    }
}
