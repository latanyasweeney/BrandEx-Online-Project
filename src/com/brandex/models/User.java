package com.brandex.models;

import com.brandex.datastructures.LinkedList;

/**
 * Abstract User class representing a generic user in the system.
 * Stores common information like name, email, and password.
 */
public abstract class User {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password; // Hashed password
    private LinkedList<String> passwordHistory; // Stores last 2 passwords

    public User(String id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.passwordHistory = new LinkedList<>();
        // Add current password to history initially
        this.passwordHistory.add(password);
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    
    public LinkedList<String> getPasswordHistory() {
        return passwordHistory;
    }

    public void setPassword(String newPassword) {
        // Keep history size at most 2
        if (passwordHistory.size() >= 2) {
            passwordHistory.removeFirst(); // Remove oldest
        }
        passwordHistory.add(newPassword);
        this.password = newPassword;
    }
    
    // Abstract method to distinguish user type
    public abstract String getRole();

    @Override
    public String toString() {
        return id + "," + firstName + "," + lastName + "," + email + "," + password;
    }
}
