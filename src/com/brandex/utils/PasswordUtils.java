package com.brandex.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.brandex.datastructures.LinkedList;

/**
 * Utilities for password hashing and validation.
 */
public class PasswordUtils {

    // Hash a password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            // Convert byte array to hex string
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Verify password match
    public static boolean verifyPassword(String inputPassword, String storedHash) {
        String inputHash = hashPassword(inputPassword);
        return inputHash.equals(storedHash);
    }

    // Check if password has been used recently (last 2)
    public static boolean isPasswordInHistory(LinkedList<String> history, String newPassword) {
        String newHash = hashPassword(newPassword);
        for (String oldHash : history) {
            if (oldHash.equals(newHash)) {
                return true;
            }
        }
        return false;
    }

    // Generate a 6-digit OTP
    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    // Generate a temporary password
    public static String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Check password strength requirements
    // Min 6 chars, 1 upper, 1 lower, 1 digit
    public static boolean isValidPassword(String password) {
        if (password.length() < 6) return false;
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
        }
        
        return hasUpper && hasLower && hasDigit;
    }
}
