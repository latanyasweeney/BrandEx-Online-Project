package com.brandex.gui;

import javax.swing.*;
import java.awt.*;
import com.brandex.models.User;
import com.brandex.models.Customer;
import com.brandex.models.Admin;
import com.brandex.services.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    private UserService userService;
    private ProductService productService;
    private CartService cartService;
    private OrderService orderService;
    
    private User currentUser;

    public MainFrame() {
        initializeServices();
        setupWindow();
        createLayout();
        addPanels();
    }

    private void initializeServices() {
        userService = new UserService();
        productService = new ProductService();
        cartService = new CartService();
        orderService = new OrderService(productService);
        orderService.loadOrdersFromFile();
    }

    private void setupWindow() {
        setTitle("BrandEx Online Store");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void createLayout() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);
    }

    private void addPanels() {
        mainPanel.add(new LoginPanel(this, userService), "LOGIN");
        mainPanel.add(new RegisterPanel(this, userService), "REGISTER");
        // Dashboards will be added/updated after login
    }

    public void showLoginPanel() {
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void showRegisterPanel() {
        cardLayout.show(mainPanel, "REGISTER");
    }

    public void showCustomerDashboard(Customer customer) {
        this.currentUser = customer;
        CustomerDashboard dashboard = new CustomerDashboard(this, customer, productService, cartService, orderService);
        mainPanel.add(dashboard, "CUSTOMER");
        cardLayout.show(mainPanel, "CUSTOMER");
    }

    public void showAdminDashboard(Admin admin) {
        this.currentUser = admin;
        AdminDashboard dashboard = new AdminDashboard(this, admin, productService, orderService, userService);
        mainPanel.add(dashboard, "ADMIN");
        cardLayout.show(mainPanel, "ADMIN");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
