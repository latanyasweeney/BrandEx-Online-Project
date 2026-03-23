package com.brandex.services;

import com.brandex.models.*;
import com.brandex.datastructures.*;
import com.brandex.utils.*;

/**
 * Handles order processing using a FIFO Queue.
 *
 * Data structures used:
 *   Queue<Order>       - FIFO queue for pending orders (admin processes front first)
 *   LinkedList<Order>  - full order history used for file persistence
 *
 * BUG FIXES applied here:
 *   1. orderHistory is now loaded from file at startup (was empty before -> orders were lost on save)
 *   2. Queue is now built from orderHistory so both structures share the SAME Order objects
 *      -> setting status on a dequeued order automatically updates the history entry too
 *   3. Stock is decremented when an order is processed and products are re-saved
 *   4. viewPendingOrders now iterates the full queue (Queue is now Iterable)
 */
public class OrderService {

    private Queue<Order>       orderQueue;   // PENDING orders only — processed FIFO
    private LinkedList<Order>  orderHistory; // ALL orders — used to persist the full file
    private ProductService     productService; // needed to save updated stock

    public OrderService(ProductService productService) {
        this.productService = productService;

        // Load ALL orders (PENDING + PROCESSED) into history first.
        // This is critical: if we only tracked orders placed in the current session,
        // every save() call would overwrite the file and erase all previous orders.
        this.orderHistory = FileHandler.loadAllOrders(productService.getBST());

        // Build the FIFO queue from history — only PENDING orders enter the queue.
        // We reuse the same Order objects so a status update on a dequeued order
        // is visible in orderHistory without any extra searching.
        this.orderQueue = new Queue<>();
        for (Order o : orderHistory) {
            if (o.getStatus().equals("PENDING")) {
                orderQueue.enqueue(o);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Customer places an order
    // -------------------------------------------------------------------------
    public void placeOrder(Customer customer, CartService cartService) {
        if (customer.getCart().isEmpty()) {
            System.out.println("Cart is empty!");
            return;
        }

        // Snapshot cart items into a new list for the order record
        LinkedList<Product> orderItems = new LinkedList<>();
        for (Product p : customer.getCart()) {
            orderItems.add(p);
        }

        String orderId = "ORD" + System.currentTimeMillis();
        double total   = cartService.calculateTotal();

        Order newOrder = new Order(orderId, customer.getId(), orderItems, total);

        // Add to queue (FIFO) and to history — SAME object reference
        orderQueue.enqueue(newOrder);
        orderHistory.add(newOrder);

        // Persist and notify
        saveOrders();
        EmailService.sendOrderConfirmation(customer.getEmail(), orderId, total);

        System.out.println("Order placed successfully! Order ID: " + orderId);
        System.out.println("Total: $" + String.format("%.2f", total));
        cartService.clearCart();
    }

    // -------------------------------------------------------------------------
    // Admin processes the next order (FIFO)
    // -------------------------------------------------------------------------
    public void processNextOrder() {
        if (orderQueue.isEmpty()) {
            System.out.println("No pending orders in queue.");
            return;
        }

        Order order = orderQueue.dequeue(); // removes from front
        order.setStatus("PROCESSED");       // also updates the history entry (same object)

        // Decrement stock for every item in this order
        for (Product p : order.getItems()) {
            if (p.getStock() > 0) {
                p.setStock(p.getStock() - 1);
            }
        }
        // Persist updated stock to products file
        FileHandler.saveProducts(productService.getAllProducts());

        System.out.println("\n--- Order Processed ---");
        System.out.println("Order ID : " + order.getOrderId());
        System.out.println("Customer : " + order.getCustomerId());
        System.out.println("Total    : $" + String.format("%.2f", order.getTotalAmount()));
        System.out.println("Items    :");
        for (Product p : order.getItems()) {
            System.out.println("  - " + p.getName());
        }

        saveOrders(); // re-save with updated PROCESSED status
    }

    // -------------------------------------------------------------------------
    // View all pending orders without removing them
    // -------------------------------------------------------------------------
    public void viewPendingOrders() {
        if (orderQueue.isEmpty()) {
            System.out.println("No pending orders.");
            return;
        }

        System.out.println("\n--- Pending Orders (FIFO Queue) ---");
        System.out.println("Orders are processed from top to bottom.\n");
        int position = 1;
        for (Order order : orderQueue) { // Queue is now Iterable
            System.out.println(position + ". " + order);
            position++;
        }
        System.out.println("\nTotal pending: " + orderQueue.size());
    }

    // -------------------------------------------------------------------------
    // Persist the full order list (history) to file
    // -------------------------------------------------------------------------
    private void saveOrders() {
        FileHandler.saveOrdersList(orderHistory);
    }
}
