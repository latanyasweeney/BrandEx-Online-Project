package com.brandex.main;

import java.util.Scanner;
import com.brandex.models.*;
import com.brandex.services.*;
import com.brandex.utils.PasswordUtils;
import com.brandex.utils.EmailService;
import com.brandex.datastructures.LinkedList;

/**
 * Main application class.
 * Handles the console UI and menu navigation.
 */
public class BrandExApp {
    private static Scanner scanner = new Scanner(System.in);
    private static UserService userService;
    private static ProductService productService;
    private static OrderService orderService;
    private static CartService cartService;

    public static void main(String[] args) {
        // Initialize services
        userService = new UserService();
        productService = new ProductService();
        orderService = new OrderService(productService);
        orderService.loadOrdersFromFile();
        cartService = new CartService();

        System.out.println("Welcome to BrandEx Online Store!");
        System.out.println("1. Launch GUI (Java Swing)");
        System.out.println("2. Launch Console UI");
        System.out.print("Choose your interface: ");
        
        String mode = scanner.nextLine();
        if (mode.equals("1")) {
            com.brandex.gui.MainFrame.main(args);
        } else {
            runConsoleApp();
        }
    }

    private static void runConsoleApp() {
        boolean running = true;
        while (running) {
            clearScreen();
            System.out.println("=== MAIN MENU (Console Mode) ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Forgot Password");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleRegister();
                    break;
                case "3":
                    handleForgotPassword();
                    break;
                case "4":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option.");
                    pause();
            }
        }
    }

    private static void handleLogin() {
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String pass = scanner.nextLine();

        User user = userService.login(email, pass);
        if (user != null) {
            System.out.println("Login successful! Welcome " + user.getFirstName());
            pause();
            
            if (user instanceof Admin) {
                showAdminMenu((Admin) user);
            } else {
                showCustomerMenu((Customer) user);
            }
        } else {
            System.out.println("Invalid credentials.");
            pause();
        }
    }

    private static void handleRegister() {
        System.out.println("\n--- REGISTRATION ---");
        System.out.print("First Name: ");
        String first = scanner.nextLine();
        System.out.print("Last Name: ");
        String last = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        Customer customer = userService.register(first, last, email);
        if (customer != null) {
            System.out.print("Enter the OTP sent to your email: ");
            String inputOtp = scanner.nextLine();
            
            if (userService.verifyOTP(customer, inputOtp)) {
                System.out.print("Enter new password: ");
                String pass = scanner.nextLine();
                userService.forceSetPassword(customer, pass);
                System.out.println("Registration successful! Please login.");
            } else {
                System.out.println("Invalid OTP.");
            }
        } else {
            System.out.println("Registration failed.");
        }
        pause();
    }
    
    private static void handleForgotPassword() {
        System.out.print("First Name: "); String first = scanner.nextLine();
        System.out.print("Last Name: "); String last = scanner.nextLine();
        System.out.print("Email: "); String email = scanner.nextLine();
        userService.forgotPassword(first, last, email);
        System.out.println("If account exists, temporary password sent.");
        pause();
    }

    private static void showCustomerMenu(Customer customer) {
        boolean loggedIn = true;
        while (loggedIn) {
            clearScreen();
            System.out.println("=== CUSTOMER MENU (" + customer.getFirstName() + ") ===");
            System.out.println("1. Browse Products");
            System.out.println("2. Search Product by Name");
            System.out.println("3. Search Product by ID");
            System.out.println("4. View Cart / Checkout");
            System.out.println("5. Change Password");
            System.out.println("6. Logout");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    browseProducts(customer);
                    break;
                case "2":
                    searchByName(customer);
                    break;
                case "3":
                    searchById(customer);
                    break;
                case "4":
                    handleCart(customer);
                    break;
                case "5":
                    handleChangePassword(customer);
                    break;
                case "6":
                    loggedIn = false;
                    break;
            }
        }
    }

    private static void browseProducts(Customer customer) {
        clearScreen();
        System.out.println("=== PRODUCT CATALOG ===");
        for (Product p : productService.getAllProducts()) {
            System.out.println(p);
        }

        System.out.print("\nEnter ID to add, or 'back': ");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("back")) return;

        Product p = productService.searchById(input);
        if (p != null) {
            System.out.print("Quantity: ");
            int qty = Integer.parseInt(scanner.nextLine());
            cartService.addToCart(customer, p, qty);
        }
        pause();
    }

    private static void searchByName(Customer customer) {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        for (Product p : productService.searchByName(name)) {
            System.out.println(p);
        }
        pause();
    }
    
    private static void searchById(Customer customer) {
        System.out.print("Enter ID: ");
        String id = scanner.nextLine();
        Product p = productService.searchById(id);
        if (p != null) System.out.println(p);
        else System.out.println("Not found.");
        pause();
    }

    private static void handleCart(Customer customer) {
        boolean inCart = true;
        while (inCart) {
            clearScreen();
            cartService.viewCart(customer);
            System.out.println("\n[C]heckout, [U]ndo, [R]edo, [E]mpty, [B]ack");
            String choice = scanner.nextLine().toUpperCase();
            switch (choice) {
                case "C":
                    System.out.print("Address: ");
                    String addr = scanner.nextLine();
                    orderService.createOrder(customer, addr);
                    inCart = false;
                    break;
                case "U": cartService.undo(customer); break;
                case "R": cartService.redo(customer); break;
                case "E": cartService.clearCart(customer); break;
                case "B": inCart = false; break;
            }
            pause();
        }
    }

    private static void showAdminMenu(Admin admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            clearScreen();
            System.out.println("=== ADMIN MENU ===");
            System.out.println("1. Add Product");
            System.out.println("2. Remove Product");
            System.out.println("3. View All Products");
            System.out.println("4. Process Next Order");
            System.out.println("5. View Pending Orders");
            System.out.println("6. Logout");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": handleAddProduct(); break;
                case "2":
                    System.out.print("ID to remove: ");
                    productService.removeProduct(scanner.nextLine());
                    break;
                case "3":
                    for (Product p : productService.getAllProducts()) System.out.println(p);
                    pause();
                    break;
                case "4": orderService.processNextOrder(); pause(); break;
                case "5": orderService.viewPendingOrders(); pause(); break;
                case "6": loggedIn = false; break;
            }
        }
    }

    private static void handleAddProduct() {
        System.out.print("Name: "); String name = scanner.nextLine();
        System.out.print("Category: "); String cat = scanner.nextLine();
        System.out.print("Price: "); double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: "); int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Desc: "); String desc = scanner.nextLine();
        String id = "P-" + (productService.getAllProducts().size() + 1);
        productService.addProduct(new Product(id, name, cat, price, stock, desc));
    }
    
    private static void handleChangePassword(User user) {
        System.out.print("Old Password: "); String old = scanner.nextLine();
        System.out.print("New Password: "); String newP = scanner.nextLine();
        if (userService.changePassword(user, old, newP)) System.out.println("Success.");
        else System.out.println("Failed.");
        pause();
    }
    // Utility methods for console management
    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                // Executes 'cls' command on Windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
          } else {
                // ANSI escape codes for Unix/Linux/macOS
                System.out.print("\033[H\033[2J");
                System.out.flush();
           }
      } catch (Exception e) {
          // Fallback: Print many newlines if the system command fails
           for (int i = 0; i < 50; i++) System.out.println();
       }
 }
    private static void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
