package com.brandex.services;

import com.brandex.models.*;
import com.brandex.utils.*;
import com.brandex.datastructures.LinkedList;

/**
 * Manages user authentication and registration.
 */
public class UserService {
    private LinkedList<User> users;
    private User currentUser;

    public UserService() {
        this.users = FileHandler.loadUsers();
        ensureRootAdmin();
    }

    private void ensureRootAdmin() {
        // Check if root exists
        boolean rootExists = false;
        for (User u : users) {
            if (u.getId().equals("root")) {
                rootExists = true;
                break;
            }
        }

        if (!rootExists) {
            // Add root admin with hashed password "admin"
            String hashed = PasswordUtils.hashPassword("admin");
            Admin root = new Admin("root", "Root", "Admin", "root@brandex.com", hashed);
            users.add(root);
            save();
        }
    }

    public User login(String emailOrId, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(emailOrId) || u.getId().equals(emailOrId)) {
                if (PasswordUtils.verifyPassword(password, u.getPassword())) {
                    currentUser = u;
                    return u;
                }
            }
        }
        return null;
    }

    public boolean register(String firstName, String lastName, String email, String password) {
        // Check if email already exists
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Email already registered!");
                return false;
            }
        }

        // Generate ID
        String id = "U" + (1000 + users.size());
        String hashed = PasswordUtils.hashPassword(password);
        
        Customer newCustomer = new Customer(id, firstName, lastName, email, hashed);
        users.add(newCustomer);
        save();
        return true;
    }
    
    // Find user by email for password reset
    public User findUserByEmail(String email) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }
    
    // Change password with history check
    public boolean changePassword(User user, String newPassword) {
        // Check history
        if (PasswordUtils.isPasswordInHistory(user.getPasswordHistory(), newPassword)) {
            System.out.println("Error: Cannot reuse last 2 passwords.");
            return false;
        }
        
        if (!PasswordUtils.isValidPassword(newPassword)) {
            System.out.println("Error: Password must be 6+ chars, with upper, lower, and digit.");
            return false;
        }

        user.setPassword(PasswordUtils.hashPassword(newPassword));
        save();
        return true;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
    
    public LinkedList<User> getAllUsers() {
        return users;
    }

    public void save() {
        FileHandler.saveUsers(users);
    }
}
