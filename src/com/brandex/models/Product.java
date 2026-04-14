package com.brandex.models;

/**
 * Product class represents an item in the store.
 * Implements Comparable so it can be stored in a Binary Search Tree (sorted by ID).
 */
public class Product implements Comparable<Product> {
    private String productId, name, category, description;
    private double price;
    private int stockQuantity;

    public Product(String productId, String name, String category, double price, int stockQuantity, String description) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public String getDescription() { return description; }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @Override
    public int compareTo(Product other) {
        return this.productId.compareTo(other.productId);
    }

    @Override
    public String toString() {
        return "[" + productId + "] " + name + " | " + category + " | $" + String.format("%.2f", price)
             + " | Stock: " + stockQuantity + "\n    " + description;
    }
}
