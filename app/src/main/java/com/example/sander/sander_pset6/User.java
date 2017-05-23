/**
 * Created by sander on 22-5-17.
 *
 *
 */

package com.example.sander.sander_pset6;

/**
 * Defines the User class
 */
public class User {
    private String username;
    private String uid;

    // email is needed to comply with Maxim's standards
    private String email;

    /* Default constructor used by FireBase */
    public User() {
    }

    public User(String username, String email, String uid) {
        this.username = username;
        this.email = email;
        this.uid = uid;
    }


    /* Getters and setters */

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
