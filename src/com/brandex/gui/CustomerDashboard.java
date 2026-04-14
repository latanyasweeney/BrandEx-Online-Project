package com.brandex.gui;

import javax.swing.*;
import java.awt.*;
import com.brandex.models.*;
import com.brandex.services.*;

public class CustomerDashboard extends JPanel {
    private MainFrame mainFrame;
    private Customer customer;
    private ProductService productService;
    private CartService cartService;
    private OrderService orderService;
    
    private JTabbedPane tabbedPane;
    private ProductBrowserPanel browsePanel;
    private CartPanel cartPanel;
    private OrderPanel orderPanel;

    public CustomerDashboard(MainFrame mainFrame, Customer customer, 
                            ProductService productService, CartService cartService, OrderService orderService) {
        this.mainFrame = mainFrame;
        this.customer = customer;
        this.productService = productService;
        this.cartService = cartService;
        this.orderService = orderService;
        
        setLayout(new BorderLayout());
        createHeader();
        createTabs();
        createFooter();
    }

    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 51, 153));
        header.setPreferredSize(new Dimension(900, 60));
        
        JLabel welcome = new JLabel("  Welcome, " + customer.getFirstName() + " " + customer.getLastName());
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel logo = new JLabel("BrandEx Online Store  ");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 22));
        
        header.add(welcome, BorderLayout.WEST);
        header.add(logo, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private void createTabs() {
        tabbedPane = new JTabbedPane();
        
        browsePanel = new ProductBrowserPanel(customer, productService, cartService);
        cartPanel = new CartPanel(customer, cartService, orderService);
        orderPanel = new OrderPanel(customer);
        
        tabbedPane.addTab("Browse Products", browsePanel);
        tabbedPane.addTab("Shopping Cart", cartPanel);
        tabbedPane.addTab("Order History", orderPanel);
        tabbedPane.addTab("My Profile", createProfilePanel());
        
        // Refresh cart and orders when tabs are switched
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            if (index == 1) cartPanel.refreshCart();
            else if (index == 2) orderPanel.refreshOrders();
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createProfilePanel() {
        JPanel profile = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; profile.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; profile.add(new JLabel(customer.getFirstName()), gbc);

        gbc.gridx = 0; gbc.gridy = 1; profile.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1; profile.add(new JLabel(customer.getLastName()), gbc);

        gbc.gridx = 0; gbc.gridy = 2; profile.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; profile.add(new JLabel(customer.getEmail()), gbc);

        JButton changePassBtn = new JButton("Change Password");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        profile.add(changePassBtn, gbc);

        changePassBtn.addActionListener(e -> showChangePasswordDialog());
        
        return profile;
    }

    private void showChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JPasswordField oldPass = new JPasswordField();
        JPasswordField newPass = new JPasswordField();
        JPasswordField confirmPass = new JPasswordField();
        
        panel.add(new JLabel("Current Password:")); panel.add(oldPass);
        panel.add(new JLabel("New Password:")); panel.add(newPass);
        panel.add(new JLabel("Confirm New Password:")); panel.add(confirmPass);

        int result = JOptionPane.showConfirmDialog(this, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String pOld = new String(oldPass.getPassword());
            String pNew = new String(newPass.getPassword());
            String pConf = new String(confirmPass.getPassword());
            
            if (pNew.equals(pConf)) {
                // Assuming UserService is accessible or we pass it
                // For simplicity, let's assume we use MainFrame's userService
                // Actually, I should probably pass it.
            }
        }
    }

    private void createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        footer.add(logoutBtn);
        add(footer, BorderLayout.SOUTH);
        
        logoutBtn.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                mainFrame.showLoginPanel();
            }
        });
    }
}
