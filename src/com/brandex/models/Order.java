package com.brandex.models;

import com.brandex.datastructures.ArrayList;

/**
 * Order class represents a completed purchase.
 */
public class Order {
    private String orderId;
    private String customerId;
    private ArrayList<CartItem> items;
    private double totalAmount;
    private String orderDate;
    private String status; // PENDING, PROCESSING, SHIPPED
    private String shippingAddress;

    public Order(String orderId, String customerId, ArrayList<CartItem> items, String orderDate, String status, String shippingAddress) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.orderDate = orderDate;
        this.status = status;
        this.shippingAddress = shippingAddress;
        calculateTotal();
    }

    public void calculateTotal() {
        this.totalAmount = 0;
        for (CartItem item : items) {
            this.totalAmount += item.getTotalPrice();
        }
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public ArrayList<CartItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public String getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public String getShippingAddress() { return shippingAddress; }

    public void setStatus(String status) {
        this.status = status;
    }

    // AI Assistance (Gemini CLI): Added this setter to fix a "method undefined" 
    // error in FileHandler when loading orders from the text file.
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return orderId + " | Customer: " + customerId + " | Total: $" + String.format("%.2f", totalAmount) + " | Status: " + status;
    }
}
