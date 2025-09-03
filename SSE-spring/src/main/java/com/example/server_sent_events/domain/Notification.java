package com.example.server_sent_events.domain;

public class Notification {

    private String type;
    private String message;

    public Notification(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getMessageJson() {
        return "{" +
                "\"type\":\"" + this.type + "\"," +
                "\"message\":\"" + this.message + "\"" +
                "}";
    }

}
