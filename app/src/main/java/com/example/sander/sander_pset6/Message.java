package com.example.sander.sander_pset6;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sander on 18-5-17.
 *
 * Message object
 */

public class Message {
    private String username;
    private String text;

    /* constructors */

    public Message() {
        // Default constructor for FB to use
    }

    public Message(String username, String text) {
        this.username = username;
        this.text = text;

    }

    /* getters and setters */

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /* other */

    @Override
    public String toString() {
        return "Message{" +
                "username='" + username + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    // prepare message for FB conventions
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("text", text);
        return result;
    }
}
