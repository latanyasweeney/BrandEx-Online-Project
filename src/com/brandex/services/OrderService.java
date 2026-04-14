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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handles order processing using a FIFO Queue.
 */
public class OrderService {

    private Queue<Order> orderQueue;
    private ArrayList<Order> processedOrders;
    private ProductService productService;

    public OrderService(ProductService productService) {
        this.productService = productService;
        this.orderQueue = new Queue<>();
        this.processedOrders = new ArrayList<>();
    }

    public Order createOrder(Customer customer, String shippingAddress) {
        if (customer.getCart().isEmpty()) {
            return null;
        }

        String orderId = "ORD-" + String.format("%03d", (orderQueue.size() + processedOrders.size() + 1));
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Deep copy items from cart
        ArrayList<CartItem> orderItems = new ArrayList<>();
        for (CartItem item : customer.getCart()) {
            orderItems.add(new CartItem(item.getProduct(), item.getQuantity()));
        }

        Order newOrder = new Order(orderId, customer.getId(), orderItems, date, "PENDING", shippingAddress);
        
        orderQueue.enqueue(newOrder);
        customer.getOrderHistory().add(newOrder);
        
        // Save orders
        FileHandler.saveOrders(orderQueue);
        
        return newOrder;
    }

    // Requirement (d): Warehouse staff process orders in FIFO order
    // This method takes the first order from the queue and ships it.
    public Order processNextOrder() {
        if (orderQueue.isEmpty()) {
            System.out.println("No pending orders.");
            return null;
        }

        // Dequeue gets the first one (FIFO)
        Order order = orderQueue.dequeue();
        order.setStatus("PROCESSING");
        
        // We reduce the stock levels for each item in the order
        for (CartItem item : order.getItems()) {
            Product p = item.getProduct();
            p.setStockQuantity(p.getStockQuantity() - item.getQuantity());
        }
        
        order.setStatus("SHIPPED");
        processedOrders.add(order);
        
        // Requirement (d): "Customers receive updates once their order is shipped via email."
        // We simulate sending the email here.
        EmailService.sendShippingUpdate(order.getCustomerId(), order.getOrderId());
        
        // Save updated products and orders back to files
        FileHandler.saveProducts(productService.getAllProducts());
        FileHandler.saveOrders(orderQueue);
        
        return order;
    }

    public void processAllOrders() {
        int count = 0;
        while (!orderQueue.isEmpty()) {
            processNextOrder();
            count++;
        }
        System.out.println("Processed " + count + " orders.");
    }

    public void viewPendingOrders() {
        if (orderQueue.isEmpty()) {
            System.out.println("No pending orders.");
            return;
        }
        // AI Assistance (Gemini CLI): Helped optimize this traversal by making Queue iterable
        for (Order o : orderQueue) {
            System.out.println(o);
        }
    }

    // AI Assistance (Gemini CLI): Added this method to help AdminDashboard show real-time orders
    public Queue<Order> getPendingOrders() {
        return orderQueue;
    }

    public void loadOrdersFromFile() {
        this.orderQueue = FileHandler.loadOrders(productService);
        System.out.println("DEBUG: Loaded " + orderQueue.size() + " pending orders from file.");
    }
}
