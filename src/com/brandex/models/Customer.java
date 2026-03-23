package com.brandex.models;

import com.brandex.datastructures.LinkedList;
import com.brandex.datastructures.Stack;

/**
 * Customer class extends User.
 * Adds shopping cart and undo/redo functionality.
 */
public class Customer extends User {
    private LinkedList<Product> cart;
    // Stacks for Undo/Redo actions
    // We will store "Action" objects or just the Product involved
    // For simplicity, let's store the Product that was last added/removed
    private Stack<Product> undoStack; 
    private Stack<String> actionStack; // "ADD" or "REMOVE"
    
    private Stack<Product> redoProductStack;
    private Stack<String> redoActionStack;

    public Customer(String id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email, password);
        this.cart = new LinkedList<>();
        this.undoStack = new Stack<>();
        this.actionStack = new Stack<>();
        this.redoProductStack = new Stack<>();
        this.redoActionStack = new Stack<>();
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }

    public LinkedList<Product> getCart() {
        return cart;
    }
    
    public Stack<Product> getUndoStack() { return undoStack; }
    public Stack<String> getActionStack() { return actionStack; }
    public Stack<Product> getRedoProductStack() { return redoProductStack; }
    public Stack<String> getRedoActionStack() { return redoActionStack; }
}
