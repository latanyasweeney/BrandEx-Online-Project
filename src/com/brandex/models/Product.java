package com.brandex.models;

/**
 * Product class represents an item in the store.
 * Implements Comparable so it can be stored in a Binary Search Tree (sorted by ID).
 */
public class Product implements Comparable<Product> {
    private String id;
    private String name;
    private String category;
    private double price;
    private int stock;
    private String description;

    public Product(String id, String name, String category, double price, int stock, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getDescription() { return description; }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public int compareTo(Product other) {
        // Compare string IDs lexicographically
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name + " | " + category + " | $" + String.format("%.2f", price)
             + " | Stock: " + stock + "\n    " + description;
    }
    
    // Check equality based on ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product other = (Product) obj;
        return id.equals(other.id);
    }
}
