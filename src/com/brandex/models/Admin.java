package com.brandex.models;

/**
 * Admin user with special privileges.
 */
public class Admin extends User {

    public Admin(String userId, String firstName, String lastName, String email, String passwordHash) {
        super(userId, firstName, lastName, email, passwordHash);
    }

    @Override
    public String getUserType() {
        return "ADMIN";
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    public boolean isRoot() {
        return getUserId().equals("root");
    }
}
