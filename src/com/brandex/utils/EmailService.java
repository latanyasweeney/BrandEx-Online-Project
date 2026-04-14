package com.brandex.utils;

import com.brandex.models.Order;

/**
 * Requirement (e): Email updates
 * This class just "simulates" sending emails by printing them to the 
 * console. The project asks for emails to be sent for things like 
 * OTP codes, password resets, and shipping updates.
 */
public class EmailService {
    
    // Requirement (e): "system will generate a One Time Password (OTP)"
    // Simulates sending the OTP to the user's signup email
    public static void sendOTP(String email, String otp) {
        System.out.println("EMAIL SENT to [" + email + "]: Your OTP code is: " + otp);
    }

    // Requirement (e): "new password should be EMAILED to the SIGNUP EMAIL"
    // used if the customer clicks "forgot password"
    public static void sendNewPassword(String email, String tempPwd) {
        System.out.println("EMAIL SENT to [" + email + "]: Your temporary password is: " + tempPwd + " (Please change this when you login).");
    }

    // Confirmation that the checkout was successful
    public static void sendOrderConfirmation(String email, Order order) {
        System.out.println("EMAIL SENT to [" + email + "]: Thanks for your order! Order ID: " + order.getOrderId() + " Total: $" + String.format("%.2f", order.getTotalAmount()));
    }

    // Requirement (d): "Customers receive updates once their order is shipped via email."
    // warehouse staff call this when they process an order
    public static void sendShippingUpdate(String email, String orderId) {
        System.out.println("EMAIL SENT to [" + email + "]: Your order " + orderId + " is on its way! It has been shipped.");
    }
}

