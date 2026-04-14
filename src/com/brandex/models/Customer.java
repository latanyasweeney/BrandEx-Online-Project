package com.brandex.models;

import com.brandex.datastructures.LinkedList;
import com.brandex.datastructures.Stack;

/**
 * Customer class extends User.
 * Adds shopping cart and undo/redo functionality.
 */
public class Customer extends User {
    private LinkedList<CartItem> cart;
    private LinkedList<Order> orderHistory;
    private Stack<Object[]> undoStack; 
    private Stack<Object[]> redoStack;
    
    private String otp;
    private boolean otpUsed;
    private boolean passwordChanged;

    public Customer(String userId, String firstName, String lastName, String email, String passwordHash) {
        super(userId, firstName, lastName, email, passwordHash);
        this.cart = new LinkedList<>();
        this.orderHistory = new LinkedList<>();
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.otpUsed = false;
        this.passwordChanged = false;
    }

    @Override
    public String getUserType() {
        return "CUSTOMER";
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }

    public LinkedList<CartItem> getCart() { return cart; }
    public LinkedList<Order> getOrderHistory() { return orderHistory; }
    public Stack<Object[]> getUndoStack() { return undoStack; }
    public Stack<Object[]> getRedoStack() { return redoStack; }
    
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public boolean isOtpUsed() { return otpUsed; }
    public void setOtpUsed(boolean otpUsed) { this.otpUsed = otpUsed; }
    public boolean isPasswordChanged() { return passwordChanged; }
    public void setPasswordChanged(boolean passwordChanged) { this.passwordChanged = passwordChanged; }
}
