package com.brandex.services;

import com.brandex.models.*;
import com.brandex.datastructures.*;

/**
 * Manages the shopping cart, including undo/redo functionality.
 */
public class CartService {

    public CartService() {}

    public void addToCart(Customer customer, Product product, int quantity) {
        if (product.getStockQuantity() < quantity) {
            System.out.println("Not enough stock!");
            return;
        }

        // Check if item already in cart
        CartItem existingItem = null;
        for (CartItem item : customer.getCart()) {
            if (item.getProduct().getProductId().equals(product.getProductId())) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            int oldQty = existingItem.getQuantity();
            existingItem.setQuantity(oldQty + quantity);
            // Push UPDATE action: [type, product, oldQty, newQty]
            customer.getUndoStack().push(new Object[]{"UPDATE", product, oldQty, oldQty + quantity});
        } else {
            CartItem newItem = new CartItem(product, quantity);
            customer.getCart().add(newItem);
            // Push ADD action: [type, product, oldQty, newQty]
            customer.getUndoStack().push(new Object[]{"ADD", product, 0, quantity});
        }
        
        // Clear redo stack
        customer.getRedoStack().clear();
        System.out.println("Added " + quantity + " x " + product.getName() + " to cart.");
    }
    
    public void removeFromCart(Customer customer, String productId) {
        CartItem toRemove = null;
        int index = 0;
        for (CartItem item : customer.getCart()) {
            if (item.getProduct().getProductId().equals(productId)) {
                toRemove = item;
                break;
            }
            index++;
        }

        if (toRemove != null) {
            customer.getCart().remove(index);
            // Push REMOVE action: [type, product, oldQty, newQty]
            customer.getUndoStack().push(new Object[]{"REMOVE", toRemove.getProduct(), toRemove.getQuantity(), 0});
            customer.getRedoStack().clear();
            System.out.println("Removed " + toRemove.getProduct().getName() + " from cart.");
        } else {
            System.out.println("Product not in cart.");
        }
    }

    public boolean undo(Customer customer) {
        if (customer.getUndoStack().isEmpty()) {
            return false;
        }

        Object[] action = customer.getUndoStack().pop();
        String type = (String) action[0];
        Product product = (Product) action[1];
        int oldQty = (int) action[2];
        int newQty = (int) action[3];

        // Based on action type, do the inverse
        if (type.equals("ADD")) {
            // Undo ADD (0 -> newQty) means set quantity to 0 (remove)
            removeFromCartWithoutStack(customer, product.getProductId());
        } else if (type.equals("REMOVE")) {
            // Undo REMOVE (oldQty -> 0) means set quantity to oldQty (add back)
            addToCartWithoutStack(customer, product, oldQty);
        } else if (type.equals("UPDATE")) {
            // Undo UPDATE (oldQty -> newQty) means set back to oldQty
            updateQuantityWithoutStack(customer, product.getProductId(), oldQty);
        }

        customer.getRedoStack().push(action);
        return true;
    }

    public boolean redo(Customer customer) {
        if (customer.getRedoStack().isEmpty()) {
            return false;
        }

        Object[] action = customer.getRedoStack().pop();
        String type = (String) action[0];
        Product product = (Product) action[1];
        int oldQty = (int) action[2];
        int newQty = (int) action[3];

        // Based on action type, re-apply
        if (type.equals("ADD")) {
            addToCartWithoutStack(customer, product, newQty);
        } else if (type.equals("REMOVE")) {
            removeFromCartWithoutStack(customer, product.getProductId());
        } else if (type.equals("UPDATE")) {
            updateQuantityWithoutStack(customer, product.getProductId(), newQty);
        }

        customer.getUndoStack().push(action);
        return true;
    }

    private void addToCartWithoutStack(Customer customer, Product product, int quantity) {
        customer.getCart().add(new CartItem(product, quantity));
    }

    private void removeFromCartWithoutStack(Customer customer, String productId) {
        int index = 0;
        for (CartItem item : customer.getCart()) {
            if (item.getProduct().getProductId().equals(productId)) {
                customer.getCart().remove(index);
                return;
            }
            index++;
        }
    }

    private void updateQuantityWithoutStack(Customer customer, String productId, int quantity) {
        for (CartItem item : customer.getCart()) {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public void viewCart(Customer customer) {
        if (customer.getCart().isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        System.out.println("\n--- Your Cart ---");
        for (CartItem item : customer.getCart()) {
            System.out.println(item);
        }
        System.out.println("Total: $" + String.format("%.2f", getCartTotal(customer)));
    }

    public void clearCart(Customer customer) {
        customer.getCart().clear();
        customer.getUndoStack().clear();
        customer.getRedoStack().clear();
    }
    
    public double getCartTotal(Customer customer) {
        double total = 0;
        for (CartItem item : customer.getCart()) {
            total += item.getTotalPrice();
        }
        return total;
    }
}
