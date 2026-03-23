package com.brandex.models;

import java.util.Date;
import com.brandex.datastructures.LinkedList;

/**
 * Order class represents a completed purchase.
 */
public class Order {
    private String orderId;
    private String customerId;
    private LinkedList<Product> items;
    private double totalAmount;
    private Date date;
    private String status; // "PENDING", "PROCESSED"

    public Order(String orderId, String customerId, LinkedList<Product> items, double totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.date = new Date();
        this.status = "PENDING";
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public LinkedList<Product> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public Date getDate() { return date; }
    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        // Simplified toString for file storage or display
        return orderId + " | Customer: " + customerId + " | Total: $" + String.format("%.2f", totalAmount) + " | Status: " + status;
    }
}
