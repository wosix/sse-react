package com.example.server_sent_events.domain;

import jakarta.servlet.http.Cookie;

public class Constants {

    private Constants() {
    }

    public static final String SSE_EMITTER_ATTRIBUTE = "sseEmitter";

    public static final String NOTIFICATION_TYPE_SUCCESS = "success";
    public static final String NOTIFICATION_TYPE_INFO = "info";
    public static final String NOTIFICATION_TYPE_WARNING = "warning";
    public static final String NOTIFICATION_TYPE_ERROR = "error";


    public static String getCookiesAsString(Cookie[] cookies) {
        if (cookies == null) {
            return "No cookies";
        }

        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookies) {
            sb.append(cookie.getName())
                    .append("=")
                    .append(cookie.getValue())
                    .append("; ");
        }
        return sb.toString();
    }
}
