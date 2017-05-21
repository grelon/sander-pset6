package com.example.sander.sander_pset6;

/**
 * Created by sander on 18-5-17.
 */

public class Message {
    private String username;
    private String text;

    public Message() {
        // Default constructor for FB to use
    }

    public Message(String username, String text) {
        this.username = username;
        this.text = text;
    }

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

    @Override
    public String toString() {
        return "Message{" +
                "username='" + username + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
