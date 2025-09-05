package com.example.server_sent_events.domain;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static com.example.server_sent_events.domain.Constants.SSE_EMITTER_ATTRIBUTE;

@Component
@RequiredArgsConstructor
public class NotificationManager implements Runnable {

    private final HttpSessionRegistry sessionRegistry;
    private final BlockingQueue<Notification> pendingNotifications = new LinkedBlockingQueue<>(100);
    private final int threadCount = 3;
    private final ExecutorService consumerExecutor = Executors.newFixedThreadPool(threadCount);

    @PostConstruct
    private void startConsumerThreads() {
        for (int i = 0; i < threadCount; i++) {
            consumerExecutor.execute(this);
        }
        System.out.println("NotificationManager started with " + threadCount + " consumers/threads");
    }

    @PreDestroy
    public void cleanup() {
        consumerExecutor.shutdown();
        pendingNotifications.clear();
    }

    @Override
    public void run() {
        while (true) {
            try {
                showSessionsLastAccessTimes();
                Notification notification = pendingNotifications.take();
                if (!sessionRegistry.hasEmitter(notification.getJSessionId())) {
                    continue;
                }
                sendNotification(notification);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " - thread interrupted");
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " - " + e.getMessage());
            }
        }
    }

    public void addNotification(Notification notification) {
        pendingNotifications.add(notification);
        System.out.println("QUEUED - " + notification);
    }

    private void sendNotification(Notification notification) {
        HttpSession session = sessionRegistry.find(notification.getJSessionId());
        if (session != null) {
            SseEmitter emitter = (SseEmitter) session.getAttribute(SSE_EMITTER_ATTRIBUTE);
            if (emitter != null) {
                try {
                    emitter.send(notification.getMessageJson());

                    System.out.println("SENT - " + Thread.currentThread().getName() + " : " + notification);
                } catch (Exception e) {
                    System.err.println("Failed to send to session " + notification.getJSessionId() + ": " + e.getMessage());
                }
            }
        }
    }

    private void showSessionsLastAccessTimes() {
        sessionRegistry.printAllLastAccessedTime();
    }

}
