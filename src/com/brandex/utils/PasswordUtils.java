package com.brandex.utils;

/**
 * Requirement (e): Security & Data Integrity
 * This utility class helps with keeping passwords safe.
 * The project says "NO CLEAR TEXT PASSWORD SHOULD BE STORED".
 * So we use SHA-256 hashing to scramble the passwords.
 */
public class PasswordUtils {

    // Requirement (e): "ONLY THE HASED OF THE PASSWORD"
    // This method takes a normal password and turns it into a long string of hex characters
    public static String hashPassword(String plain) {
        try {
            // SHA-256 is a strong way to hash passwords
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plain.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            
            // convert the byte array to hex format
            StringBuilder hexStr = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexStr.append('0');
                hexStr.append(hex);
            }
            return hexStr.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            // if for some reason SHA-256 isn't available
            return null;
        }
    }

    // Checking if a typed password matches the hash we have in the file
    public static boolean verifyPassword(String plain, String hash) {
        String hashedPlain = hashPassword(plain);
        return hashedPlain != null && hashedPlain.equals(hash);
    }

    // Requirement (e): "system will generate a One Time Password (OTP)"
    // Generates a random 6-digit code
    public static String generateOTP() {
        java.util.Random random = new java.util.Random();
        return String.format("%06d", random.nextInt(999999));
    }

    // Requirement (e): "last TWO passwords and no password in the history can be used"
    // checks if the new password matches anything in the user's history
    public static boolean isInHistory(String newHash, String[] history) {
        if (history == null) return false;
        for (String h : history) {
            if (h != null && newHash.equals(h)) return true;
        }
        return false;
    }

    // Basic check to stop people from typing weird characters into the fields
    public static boolean validateInput(String input) {
        if (input == null || input.trim().isEmpty()) return false;
        
        // checking for some symbols that might cause issues
        String forbidden = "'\";<>";
        for (char c : forbidden.toCharArray()) {
            if (input.indexOf(c) != -1) return false;
        }
        return true;
    }
}

