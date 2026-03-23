package com.brandex.models;

/**
 * Admin user with special privileges.
 */
public class Admin extends User {

    public Admin(String id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email, password);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
