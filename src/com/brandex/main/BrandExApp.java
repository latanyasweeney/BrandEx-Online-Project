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

        System.out.println("Welcome to BrandEx Online Store!");
        
        boolean running = true;
        while (running) {
            clearScreen();
            System.out.println("=== MAIN MENU ===");
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
        System.out.print("Enter Email or User ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Password: ");
        String pass = scanner.nextLine();

        User user = userService.login(id, pass);
        if (user != null) {
            System.out.println("Login successful! Welcome " + user.getFirstName());
            pause();
            
            if (user instanceof Admin) {
                showAdminMenu((Admin) user);
            } else {
                // Initialize cart service for this session
                cartService = new CartService((Customer) user);
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
        
        // Input validation
        if (!email.contains("@") || !email.contains(".")) {
            System.out.println("Invalid email format.");
            pause();
            return;
        }

        // OTP Verification
        String otp = PasswordUtils.generateOTP();
        EmailService.sendOTP(email, otp);
        
        System.out.print("Enter the OTP sent to your email: ");
        String inputOtp = scanner.nextLine();
        
        if (!inputOtp.equals(otp)) {
            System.out.println("Invalid OTP. Registration failed.");
            pause();
            return;
        }

        // Force password creation
        String password = promptForPassword();
        if (password == null) return;

        if (userService.register(first, last, email, password)) {
            System.out.println("Registration successful! Please login.");
        } else {
            System.out.println("Registration failed.");
        }
        pause();
    }
    
    private static void handleForgotPassword() {
        System.out.print("Enter your registered email: ");
        String email = scanner.nextLine();
        User user = userService.findUserByEmail(email);
        
        if (user != null) {
            String tempPass = PasswordUtils.generateTempPassword();
            // In a real app we'd update the password here, but hash it first
            // Note: Since we need to force change, we just set it now.
            // Let's just set it.
            user.setPassword(PasswordUtils.hashPassword(tempPass));
            userService.save();
            
            EmailService.sendTempPassword(email, tempPass);
            System.out.println("Temporary password sent to your email.");
        } else {
            System.out.println("Email not found.");
        }
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
                    browseProducts();
                    break;
                case "2":
                    searchByName();
                    break;
                case "3":
                    searchById();
                    break;
                case "4":
                    handleCart(customer);
                    break;
                case "5":
                    handleChangePassword(customer);
                    break;
                case "6":
                    userService.logout();
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid option.");
                    pause();
            }
        }
    }

    private static void browseProducts() {
        clearScreen();
        System.out.println("==============================");
        System.out.println("       PRODUCT CATALOG        ");
        System.out.println("==============================");
        LinkedList<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products available at the moment.");
        } else {
            for (Product p : products) {
                System.out.println(p);
                System.out.println("------------------------------");
            }
        }

        System.out.println("\nEnter Product ID to add to cart, or 'back' to return:");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("back")) return;

        Product p = productService.searchById(input);
        if (p != null) {
            cartService.addToCart(p);
        } else {
            System.out.println("Product not found.");
        }
        pause();
    }

    private static void searchByName() {
        System.out.print("Enter partial product name: ");
        String name = scanner.nextLine();
        productService.searchByName(name);

        // After showing results, let the customer add one to cart
        System.out.print("\nEnter Product ID to add to cart, or press Enter to go back: ");
        String input = scanner.nextLine();
        if (!input.trim().isEmpty()) {
            Product found = productService.searchById(input);
            if (found != null) {
                cartService.addToCart(found);
            } else {
                System.out.println("Product not found.");
            }
        }
        pause();
    }
    
    private static void searchById() {
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine();
        Product p = productService.searchById(id);
        if (p != null) {
            System.out.println("Found: " + p);
            System.out.print("Add to cart? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                cartService.addToCart(p);
            }
        } else {
            System.out.println("Product not found.");
        }
        pause();
    }

    private static void handleCart(Customer customer) {
        boolean inCart = true;
        while (inCart) {
            clearScreen();
            cartService.viewCart();
            System.out.println("\nOptions: [C]heckout, [U]ndo, [R]edo, [E]mpty Cart, [B]ack");
            String choice = scanner.nextLine().toUpperCase();
            
            switch (choice) {
                case "C":
                    orderService.placeOrder(customer, cartService);
                    pause();
                    inCart = false;
                    break;
                case "U":
                    cartService.undo();
                    pause();
                    break;
                case "R":
                    cartService.redo();
                    pause();
                    break;
                case "E":
                    cartService.clearCart();
                    System.out.println("Cart cleared.");
                    pause();
                    break;
                case "B":
                    inCart = false;
                    break;
            }
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
            System.out.println("4. Process Orders (FIFO)");
            System.out.println("5. View Pending Orders");
            System.out.println("6. Logout");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleAddProduct();
                    break;
                case "2":
                    System.out.print("Enter Product ID to remove: ");
                    String id = scanner.nextLine();
                    if (productService.removeProduct(id)) {
                        System.out.println("Product removed.");
                    } else {
                        System.out.println("Product not found.");
                    }
                    pause();
                    break;
                case "3":
                    for (Product p : productService.getAllProducts()) {
                        System.out.println(p);
                    }
                    pause();
                    break;
                case "4":
                    orderService.processNextOrder();
                    pause();
                    break;
                case "5":
                    orderService.viewPendingOrders();
                    pause();
                    break;
                case "6":
                    userService.logout();
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void handleAddProduct() {
        System.out.println("--- Add New Product ---");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Category: ");
        String cat = scanner.nextLine();
        
        double price = 0;
        try {
            System.out.print("Price: ");
            price = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid price.");
            return;
        }
        
        int stock = 0;
        try {
            System.out.print("Stock: ");
            stock = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid stock.");
            return;
        }
        
        System.out.print("Description: ");
        String desc = scanner.nextLine();

        productService.addProduct(name, cat, price, stock, desc);
        pause();
    }
    
    private static void handleChangePassword(User user) {
        System.out.println("--- Change Password ---");
        String newPass = promptForPassword();
        if (newPass != null) {
            if (userService.changePassword(user, newPass)) {
                System.out.println("Password changed successfully.");
            } else {
                System.out.println("Password change failed. Check requirements.");
            }
        }
        pause();
    }
    
    private static String promptForPassword() {
        System.out.print("Enter Password (min 6 chars, 1 upper, 1 lower, 1 digit): ");
        String pass = scanner.nextLine();
        if (!PasswordUtils.isValidPassword(pass)) {
            System.out.println("Password does not meet strength requirements.");
            return null;
        }
        return pass;
    }
    // Utility methods for console management
    private static void clearScreen() {
        // "Clear" console by printing newlines
        for (int i = 0; i < 5; i++) System.out.println();
    }
    
    private static void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
