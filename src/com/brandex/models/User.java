package com.brandex.models;

import com.brandex.datastructures.LinkedList;

/**
 * Abstract User class representing a generic user in the system.
 * Stores common information like name, email, and password.
 */
public abstract class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private String[] passwordHistory; // array of last 2 hashed passwords
    private String status; // ACTIVE or INACTIVE

    public User(String userId, String firstName, String lastName, String email, String passwordHash) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordHistory = new String[]{"", ""};
        this.status = "ACTIVE";
    }

    public abstract String getUserType();

    public String getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String[] getPasswordHistory() { return passwordHistory; }
    public String getStatus() { return status; }

    public void setPassword(String hash) {
        // Shift history
        passwordHistory[1] = passwordHistory[0];
        passwordHistory[0] = this.passwordHash;
        this.passwordHash = hash;
    }

    public void setStatus(String status) { this.status = status; }

    // Backwards compatibility for now
    public String getId() { return userId; }
    public String getPassword() { return passwordHash; }
    public abstract String getRole();
}
