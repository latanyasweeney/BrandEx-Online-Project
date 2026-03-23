package com.brandex.utils;

/**
 * Service to simulate sending emails.
 * Prints to console.
 */
public class EmailService {

    public static void sendOTP(String email, String otp) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("EMAIL SIMULATION - To: " + email);
        System.out.println("Subject: Your Verification Code");
        System.out.println("Body: Your OTP code is: " + otp);
        System.out.println("--------------------------------------------------\n");
    }
    
    public static void sendWelcome(String email, String name) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("EMAIL SIMULATION - To: " + email);
        System.out.println("Subject: Welcome to BrandEx!");
        System.out.println("Body: Hello " + name + ", your account is ready.");
        System.out.println("--------------------------------------------------\n");
    }
    
    public static void sendTempPassword(String email, String tempPass) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("EMAIL SIMULATION - To: " + email);
        System.out.println("Subject: Password Reset");
        System.out.println("Body: Your temporary password is: " + tempPass);
        System.out.println("Please change it upon logging in.");
        System.out.println("--------------------------------------------------\n");
    }
    
    public static void sendOrderConfirmation(String email, String orderId, double total) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("EMAIL SIMULATION - To: " + email);
        System.out.println("Subject: Order Confirmation");
        System.out.println("Body: Thank you for your order #" + orderId);
        System.out.println("Total Amount: $" + String.format("%.2f", total));
        System.out.println("--------------------------------------------------\n");
    }
}
