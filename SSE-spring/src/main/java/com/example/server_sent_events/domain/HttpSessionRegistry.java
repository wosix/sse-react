package com.example.server_sent_events.domain;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HttpSessionRegistry implements HttpSessionListener {

    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        SESSIONS.put(session.getId(), session);
        System.out.println("SESSION CREATED - " + session.getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        SESSIONS.remove(session.getId());
        System.out.println("SESSION DESTROYED - " + session.getId());
    }

    public HttpSession find(String sessionId) {
        return SESSIONS.get(sessionId);
    }

    public boolean hasEmitter(String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }

    public int getCount() {
        return SESSIONS.size();
    }

    public void printAllLastAccessedTime() {
        System.out.println("\t----- Active sessions: " + getCount() + " -----");
        SESSIONS.forEach((s, session) -> {
            printLastAccessedTime(s);
        });
        System.out.println("\t------------------------------");
    }

    public void printLastAccessedTime(String sessionId) {
        long currentTime = System.currentTimeMillis();
        HttpSession session = SESSIONS.get(sessionId);
        long lastAccess = session.getLastAccessedTime();
        long inactiveTime = (currentTime - lastAccess) / 1000;
        long maxInactive = session.getMaxInactiveInterval();

        System.out.println(String.format(
                "Session: %s, Last accessed: %d sec ago - Max inactive: %d seconds",
                session.getId(), inactiveTime, maxInactive
        ));
    }

}
