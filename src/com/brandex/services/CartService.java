package com.brandex.services;

import com.brandex.models.*;
import com.brandex.datastructures.*;

/**
 * Manages the shopping cart, including undo/redo functionality.
 */
public class CartService {

    private Customer customer;

    public CartService(Customer customer) {
        this.customer = customer;
    }

    public void addToCart(Product product) {
        if (product.getStock() <= 0) {
            System.out.println("Product out of stock!");
            return;
        }

        // Add to cart
        customer.getCart().add(product);
        
        // Push action to undo stack
        customer.getUndoStack().push(product);
        customer.getActionStack().push("ADD");
        
        // Clear redo stack because new action happened
        customer.getRedoProductStack().clear();
        customer.getRedoActionStack().clear();
        
        System.out.println("Added " + product.getName() + " to cart.");
    }
    
    public void removeFromCart(Product product) {
        if (customer.getCart().remove(product)) {
            customer.getUndoStack().push(product);
            customer.getActionStack().push("REMOVE");
            
            customer.getRedoProductStack().clear();
            customer.getRedoActionStack().clear();
            
            System.out.println("Removed " + product.getName() + " from cart.");
        } else {
            System.out.println("Product not in cart.");
        }
    }

    public void undo() {
        if (customer.getUndoStack().isEmpty()) {
            System.out.println("Nothing to undo.");
            return;
        }

        Product p = customer.getUndoStack().pop();
        String action = customer.getActionStack().pop();

        if (action.equals("ADD")) {
            // Undo ADD means REMOVE
            customer.getCart().remove(p);
            // Push to redo
            customer.getRedoProductStack().push(p);
            customer.getRedoActionStack().push("ADD");
            System.out.println("Undid ADD: " + p.getName());
            
        } else if (action.equals("REMOVE")) {
            // Undo REMOVE means ADD back
            customer.getCart().add(p);
            // Push to redo
            customer.getRedoProductStack().push(p);
            customer.getRedoActionStack().push("REMOVE");
            System.out.println("Undid REMOVE: " + p.getName());
        }
    }

    public void redo() {
        if (customer.getRedoProductStack().isEmpty()) {
            System.out.println("Nothing to redo.");
            return;
        }

        Product p = customer.getRedoProductStack().pop();
        String action = customer.getRedoActionStack().pop();

        if (action.equals("ADD")) {
            // Redo ADD
            customer.getCart().add(p);
            // Push back to undo
            customer.getUndoStack().push(p);
            customer.getActionStack().push("ADD");
            System.out.println("Redid ADD: " + p.getName());
            
        } else if (action.equals("REMOVE")) {
            // Redo REMOVE
            customer.getCart().remove(p);
            // Push back to undo
            customer.getUndoStack().push(p);
            customer.getActionStack().push("REMOVE");
            System.out.println("Redid REMOVE: " + p.getName());
        }
    }

    public void viewCart() {
        if (customer.getCart().isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        System.out.println("\n--- Your Cart ---");
        double total = 0;
        for (Product p : customer.getCart()) {
            System.out.println(p.getName() + " - $" + p.getPrice());
            total += p.getPrice();
        }
        System.out.println("Total: $" + String.format("%.2f", total));
    }

    public void clearCart() {
        customer.getCart().clear();
        customer.getUndoStack().clear();
        customer.getActionStack().clear();
        customer.getRedoProductStack().clear();
        customer.getRedoActionStack().clear();
    }
    
    public double calculateTotal() {
        double total = 0;
        for (Product p : customer.getCart()) {
            total += p.getPrice();
        }
        return total;
    }
}
