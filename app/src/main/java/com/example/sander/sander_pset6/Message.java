/*
 * Created by sander on 18-5-17.
 */

package com.example.sander.sander_pset6;

/**
 * Defines the Message class.
 * Value name 'senderId' was chosen to comply with Maxim's implementation. Unused getter and setter
 * methods are included for ease of possible future use.
 */
class Message {
    private String senderId;
    private String username;
    private String text;

    /* Constructors */
    public Message() {
        // default constructor for FB to use
    }

    Message(String username, String text, String senderId) {
        this.username = username;
        this.text = text;
        this.senderId = senderId;
    }

    /* Getters and setters */
    public String getSenderId() {
        return senderId;
    }

    void setSenderId(String senderId) { this.senderId = senderId; }

    String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    String getText() {
        return text;
    }

    void setText(String text) {
        this.text = text;
    }

    /* Other */
    @Override
    public String toString() {
        return "Message{" +
                "username='" + username + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
