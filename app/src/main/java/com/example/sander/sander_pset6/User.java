/*
 * Created by sander on 22-5-17.
 */

package com.example.sander.sander_pset6;

/**
 * Defines the User class. The email attribute is needed to comply with Maxime's standards
 *
 */
class User {
    private String username;
    private String uid;
    private String email;

    /* Constructor */
    User() {
        // default constructor for FB to use
    }

    /* Getters and setters */

    String getUid() {
        return uid;
    }

    void setUid(String uid) {
        this.uid = uid;
    }

    String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    String getEmail() {
        return email;
    }

    void setEmail(String email) {
        this.email = email;
    }

    /* Other */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
