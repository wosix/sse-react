package com.example.server_sent_events.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Notification {

    private String jSessionId;
    private String type;
    private String message;

    public String getMessageJson() {
        return "{" +
                "\"type\":\"" + this.type + "\"," +
                "\"message\":\"" + this.message + "\"" +
                "}";
    }

    @Override
    public String toString() {
        return "{Notification = " + jSessionId + " / " + getMessageJson();
    }
}
