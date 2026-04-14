package com.brandex.services;

import com.brandex.models.*;
import com.brandex.utils.*;
import com.brandex.datastructures.LinkedList;

/**
 * Requirement (a): User Account Management
 * Requirement (e): Security & Data Integrity
 * This class handles all the user stuff like signing up, logging in, 
 * and making sure passwords are safe by hashing them.
 */
public class UserService {
    private LinkedList<User> users; // list of all users
    private User currentUser;

    public UserService() {
        loadUsersFromFile(); // get users from the file when we start
    }

    // Requirement (e): Customer registration
    // Users sign up with first name, last name, and email only!
    public Customer register(String firstName, String lastName, String email) {
        // Validation to prevent bad inputs or "injection" as mentioned in (e)
        if (!PasswordUtils.validateInput(firstName) || !PasswordUtils.validateInput(lastName) || !PasswordUtils.validateInput(email)) {
            return null;
        }

        // Make sure the email isn't already used
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) return null;
        }

        String userId = "U-" + String.format("%03d", (users.size() + 1));
        
        // Requirement (e): System generates a One Time Password (OTP)
        String otp = PasswordUtils.generateOTP();
        
        // We create the customer object here
        Customer customer = new Customer(userId, firstName, lastName, email, "");
        customer.setOtp(otp); // store the OTP for verification
        
        // Send the OTP to the user's email (simulated)
        EmailService.sendOTP(email, otp);
        
        users.add(customer);
        FileHandler.saveUsers(users); // save to file
        
        return customer;
    }

    // Checking if the OTP the user typed matches what we sent
    public boolean verifyOTP(Customer customer, String enteredOtp) {
        if (customer.getOtp().equals(enteredOtp) && !customer.isOtpUsed()) {
            customer.setOtpUsed(true); // can only use it once!
            return true;
        }
        return false;
    }

    // Requirement (e): Forced password change after OTP
    public void forceSetPassword(Customer customer, String newPassword) {
        // Hashing the password so it's not clear text in the file!
        String hashed = PasswordUtils.hashPassword(newPassword);
        customer.setPassword(hashed);
        customer.setPasswordChanged(true);
        FileHandler.saveUsers(users); // update file
    }

    // Standard login method
    public User login(String emailOrId, String password) {
        for (User u : users) {
            // Can login with email or the ID
            if (u.getEmail().equalsIgnoreCase(emailOrId) || u.getUserId().equalsIgnoreCase(emailOrId)) {
                // Verify the hashed password
                if (PasswordUtils.verifyPassword(password, u.getPasswordHash())) {
                    currentUser = u;
                    return u;
                }
            }
        }
        return null; // login failed
    }

    // Requirement (e): Change password with history check
    public boolean changePassword(User user, String oldPwd, String newPwd) {
        // Check if the old password is correct first
        if (!PasswordUtils.verifyPassword(oldPwd, user.getPasswordHash())) return false;
        
        String newHash = PasswordUtils.hashPassword(newPwd);
        
        // Requirement (e): Check history (last 2 passwords can't be reused)
        if (PasswordUtils.isInHistory(newHash, user.getPasswordHistory())) return false;
        
        // All good, update it
        user.setPassword(newHash);
        FileHandler.saveUsers(users);
        return true;
    }

    // Requirement (e): Forgot password logic
    public void forgotPassword(String firstName, String lastName, String email) {
        for (User u : users) {
            // verify it's the right person
            if (u.getFirstName().equalsIgnoreCase(firstName) && 
                u.getLastName().equalsIgnoreCase(lastName) && 
                u.getEmail().equalsIgnoreCase(email)) {
                
                // generate a new temporary password
                String tempPass = PasswordUtils.generateOTP(); 
                String hashed = PasswordUtils.hashPassword(tempPass);
                u.setPassword(hashed);
                
                // Email the new password to the user
                EmailService.sendNewPassword(email, tempPass);
                
                FileHandler.saveUsers(users);
                return;
            }
        }
    }

    // Loading users from the PASSWORD FILE
    public void loadUsersFromFile() {
        this.users = FileHandler.loadUsers();
        
        // Requirement (e): Setup the root admin if it doesn't exist
        // Admin username: root, password: admin
        boolean rootFound = false;
        for (User u : users) {
            if (u.getUserId().equals("root")) {
                rootFound = true;
                break;
            }
        }
        if (!rootFound) {
            Admin root = new Admin("root", "Root", "Admin", "admin@brandex.com", PasswordUtils.hashPassword("admin"));
            users.add(root);
            FileHandler.saveUsers(users);
        }
    }

    public LinkedList<User> getAllUsers() {
        return users;
    }
}

