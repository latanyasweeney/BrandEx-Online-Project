package com.brandex.utils;

import java.io.*;
import java.util.Scanner;
import com.brandex.models.*;
import com.brandex.datastructures.*;
import com.brandex.services.ProductService;

/**
 * This class handles all our file stuff.
 * Requirement (a): passwords and user data are stored in a PASSWORD FILE.
 * Requirement (d): order data is saved to a text file for persistence.
 */
public class FileHandler {

    private static final String DATA_DIR = "data/";
    private static final String USERS_FILE = DATA_DIR + "passwords.txt"; // Requirement (a): The PASSWORD FILE
    private static final String PRODUCTS_FILE = DATA_DIR + "products.txt";
    private static final String ORDERS_FILE = DATA_DIR + "orders.txt";

    // Setup the data folder if it's not there
    static {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // Saving all users to the password file
    public static void saveUsers(LinkedList<User> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                StringBuilder sb = new StringBuilder();
                sb.append(user.getId()).append("|")
                  .append(user.getFirstName()).append("|")
                  .append(user.getLastName()).append("|")
                  .append(user.getEmail()).append("|")
                  .append(user.getPasswordHash()).append("|"); // Requirement (e): Save the HASHED password
                
                String[] history = user.getPasswordHistory();
                sb.append(history[0]).append("|")
                  .append(history[1]).append("|")
                  .append(user.getStatus());
                
                pw.println(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loading users from the file when the app starts
    public static LinkedList<User> loadUsers() {
        LinkedList<User> users = new LinkedList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length < 8) continue;

                User user;
                // Check if the user is the admin (root)
                if (parts[0].equals("root")) {
                    user = new Admin(parts[0], parts[1], parts[2], parts[3], parts[4]);
                } else {
                    user = new Customer(parts[0], parts[1], parts[2], parts[3], parts[4]);
                }
                user.getPasswordHistory()[0] = parts[5];
                user.getPasswordHistory()[1] = parts[6];
                user.setStatus(parts[7]);
                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Saving products to products.txt
    public static void saveProducts(LinkedList<Product> products) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product p : products) {
                pw.println(p.getProductId() + "|" + p.getName() + "|" + p.getCategory() + "|" + 
                           p.getPrice() + "|" + p.getStockQuantity() + "|" + p.getDescription());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loading products for the linked list and BST
    public static LinkedList<Product> loadProducts() {
        LinkedList<Product> products = new LinkedList<>();
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return products;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length < 6) continue;
                products.add(new Product(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]), 
                                       Integer.parseInt(parts[4]), parts[5]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Requirement (d): save orders to handle "high traffic efficiently"
    public static void saveOrders(Queue<Order> orders) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ORDERS_FILE))) {
            ArrayList<Order> temp = new ArrayList<>();
            // we dequeue everything to save it, then put it back
            while (!orders.isEmpty()) {
                Order o = orders.dequeue();
                temp.add(o);
                
                StringBuilder sb = new StringBuilder();
                sb.append(o.getOrderId()).append("|")
                  .append(o.getCustomerId()).append("|")
                  .append(o.getOrderDate()).append("|")
                  .append(o.getStatus()).append("|")
                  .append(o.getTotalAmount()).append("|")
                  .append(o.getShippingAddress()).append("|")
                  .append(o.getItems().size());
                
                // Save each item in the order too
                for (CartItem item : o.getItems()) {
                    sb.append("|").append(item.getProduct().getProductId())
                      .append("|").append(item.getQuantity());
                }
                pw.println(sb.toString());
            }
            // Put orders back into the queue for the Admin to see
            for (Order o : temp) {
                orders.enqueue(o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loading orders back into the Queue
    public static Queue<Order> loadOrders(ProductService productService) {
        Queue<Order> orders = new Queue<>();
        File file = new File(ORDERS_FILE);
        if (!file.exists()) return orders;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length < 7) continue;

                String orderId = parts[0];
                String custId = parts[1];
                String date = parts[2];
                String status = parts[3];
                double total = Double.parseDouble(parts[4]);
                String address = parts[5];
                int itemCount = Integer.parseInt(parts[6]);

                ArrayList<CartItem> items = new ArrayList<>();
                int current = 7;
                for (int i = 0; i < itemCount; i++) {
                    if (current + 1 < parts.length) {
                        String pid = parts[current++];
                        int qty = Integer.parseInt(parts[current++]);
                        // find the actual product using the BST search logic in productService
                        Product p = productService.searchById(pid);
                        if (p != null) {
                            items.add(new CartItem(p, qty));
                        }
                    }
                }
                Order order = new Order(orderId, custId, items, date, status, address);
                order.setTotalAmount(total); // restore the stored total
                orders.enqueue(order);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return orders;
    }
}

