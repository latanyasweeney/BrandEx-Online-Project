package com.brandex.utils;

import java.io.*;
import com.brandex.models.*;
import com.brandex.datastructures.*;

/**
 * Handles all file I/O operations.
 * Reads and writes data to text files in CSV format.
 */
public class FileHandler {

    private static final String DATA_DIR = "resources/data/";
    private static final String USERS_FILE = DATA_DIR + "passwords.txt";
    private static final String PRODUCTS_FILE = DATA_DIR + "products.txt";
    private static final String ORDERS_FILE = DATA_DIR + "orders.txt";

    // Ensure data directory exists
    static {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void saveUsers(LinkedList<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                StringBuilder line = new StringBuilder();
                line.append(user.getId()).append(",")
                    .append(user.getFirstName()).append(",")
                    .append(user.getLastName()).append(",")
                    .append(user.getEmail()).append(",")
                    .append(user.getPassword()).append(",")
                    .append(user.getRole()).append(",");
                
                // Save password history separated by semicolons
                LinkedList<String> history = user.getPasswordHistory();
                StringBuilder historyStr = new StringBuilder();
                for (String oldPass : history) {
                    historyStr.append(oldPass).append(";");
                }
                line.append(historyStr);
                
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public static LinkedList<User> loadUsers() {
        LinkedList<User> users = new LinkedList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                // ID,First,Last,Email,Pass,Role,History
                if (parts.length < 6) continue;

                String id = parts[0];
                String first = parts[1];
                String last = parts[2];
                String email = parts[3];
                String pass = parts[4];
                String role = parts[5];

                User user;
                if (role.equals("ADMIN")) {
                    user = new Admin(id, first, last, email, pass);
                } else {
                    user = new Customer(id, first, last, email, pass);
                }

                // Load history if exists
                if (parts.length > 6) {
                    String[] history = parts[6].split(";");
                    for (String h : history) {
                        if (!h.isEmpty() && !h.equals(pass)) { 
                            // Avoid adding current password again as constructor adds it
                            user.getPasswordHistory().add(h);
                        }
                    }
                }
                
                users.add(user);
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public static void saveProducts(LinkedList<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product p : products) {
                writer.write(p.getId() + "," + p.getName() + "," + p.getCategory() + "," + 
                           p.getPrice() + "," + p.getStock() + "," + p.getDescription());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving products: " + e.getMessage());
        }
    }

    public static LinkedList<Product> loadProducts() {
        LinkedList<Product> products = new LinkedList<>();
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return products;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 6) continue;

                // Handle description that might contain commas
                String description = parts[5];
                if (parts.length > 6) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(parts[5]);
                    for (int i = 6; i < parts.length; i++) {
                        sb.append(",").append(parts[i]);
                    }
                    description = sb.toString();
                }

                Product p = new Product(parts[0], parts[1], parts[2], 
                                      Double.parseDouble(parts[3]), 
                                      Integer.parseInt(parts[4]), 
                                      description);
                products.add(p);
            }
        } catch (IOException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
        return products;
    }
    
    // Save orders (Queue)
    public static void saveOrders(Queue<Order> orderQueue) {
        // Since Queue removes items on dequeue, we should just iterate carefully or
        // assume the service passes us a list representing the queue.
        // But our Queue doesn't expose iterator nicely? Ah, LinkedList does.
        // Wait, Queue implementation uses LinkedList internally but doesn't expose it.
        // However, this is a student project. We can modify Queue to be Iterable 
        // or just dump the "pending" orders.
        // Actually, let's just assume we save the persistent list of orders.
        // But for this project, let's save the queue state.
        
        // We can't iterate our custom Queue without popping.
        // Let's rely on the service to pass a LinkedList of orders to save.
        // OR better, let's make Queue iterable or accessible.
        // I'll assume we pass a LinkedList<Order> here for simplicity.
        // The Service will maintain the history list anyway.
    }
    
    // Overloaded for LinkedList (Order History)
    public static void saveOrdersList(LinkedList<Order> orders) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDERS_FILE))) {
            for (Order order : orders) {
                StringBuilder sb = new StringBuilder();
                sb.append(order.getOrderId()).append(",")
                  .append(order.getCustomerId()).append(",")
                  .append(order.getTotalAmount()).append(",")
                  .append(order.getStatus()).append(",");
                
                // Save product IDs
                StringBuilder itemsStr = new StringBuilder();
                for (Product p : order.getItems()) {
                    itemsStr.append(p.getId()).append(";");
                }
                sb.append(itemsStr);
                
                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving orders: " + e.getMessage());
        }
    }

    // Load ALL orders (PENDING + PROCESSED) as a LinkedList.
    // Used by OrderService to rebuild the history list on startup so saves are complete.
    public static LinkedList<Order> loadAllOrders(BinarySearchTree<Product> productCatalog) {
        LinkedList<Order> allOrders = new LinkedList<>();
        File file = new File(ORDERS_FILE);
        if (!file.exists()) return allOrders;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String orderId = parts[0];
                String custId  = parts[1];
                double total   = Double.parseDouble(parts[2]);
                String status  = parts[3];

                LinkedList<Product> items = new LinkedList<>();
                String[] itemIds = parts[4].split(";");
                for (String pid : itemIds) {
                    if (pid.isEmpty()) continue;
                    Product searchKey = new Product(pid, "", "", 0, 0, "");
                    Product found = productCatalog.search(searchKey);
                    if (found != null) items.add(found);
                }

                Order order = new Order(orderId, custId, items, total);
                order.setStatus(status);
                allOrders.add(order);
            }
        } catch (IOException e) {
            System.out.println("Error loading all orders: " + e.getMessage());
        }
        return allOrders;
    }

    // Load orders. Needs product catalog to reconstruct product objects.
    public static Queue<Order> loadOrders(BinarySearchTree<Product> productCatalog) {
        Queue<Order> orderQueue = new Queue<>();
        File file = new File(ORDERS_FILE);
        if (!file.exists()) return orderQueue;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String orderId = parts[0];
                String custId = parts[1];
                double total = Double.parseDouble(parts[2]);
                String status = parts[3];
                
                LinkedList<Product> items = new LinkedList<>();
                if (parts.length > 4) {
                    String[] itemIds = parts[4].split(";");
                    for (String pid : itemIds) {
                        // Create dummy product for search key
                        // The BST search needs a comparable object.
                        // Our BST search takes T data. 
                        // Product compares by ID. So we can create a dummy product with just ID.
                        Product searchKey = new Product(pid, "", "", 0, 0, "");
                        Product found = productCatalog.search(searchKey);
                        if (found != null) {
                            items.add(found);
                        }
                    }
                }

                Order order = new Order(orderId, custId, items, total);
                order.setStatus(status);
                
                // Only load pending orders into the active queue?
                // Or maybe we want to load all?
                // For a queue, we usually only care about PENDING.
                if (status.equals("PENDING")) {
                    orderQueue.enqueue(order);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading orders: " + e.getMessage());
        }
        return orderQueue;
    }
}
