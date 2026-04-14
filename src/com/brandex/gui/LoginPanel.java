package com.brandex.gui;

import javax.swing.*;
import java.awt.*;
import com.brandex.services.UserService;
import com.brandex.models.User;
import com.brandex.models.Customer;
import com.brandex.models.Admin;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private UserService userService;
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton, forgotButton;
    private JLabel messageLabel;

    public LoginPanel(MainFrame mainFrame, UserService userService) {
        this.mainFrame = mainFrame;
        this.userService = userService;
        
        setLayout(new GridBagLayout());
        setBackground(new Color(230, 240, 255)); // Light blue
        
        createComponents();
    }

    private void createComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Welcome to BrandEx", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 128, 128)); // Teal
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Email
        gbc.gridwidth = 1; gbc.gridy = 1;
        add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Login Button
        loginButton = new JButton("Login");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(loginButton, gbc);

        // Register Link
        registerButton = new JButton("Don't have an account? Register");
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setForeground(Color.BLUE);
        gbc.gridy = 4;
        add(registerButton, gbc);

        // Forgot Password
        forgotButton = new JButton("Forgot Password?");
        forgotButton.setBorderPainted(false);
        forgotButton.setContentAreaFilled(false);
        gbc.gridy = 5;
        add(forgotButton, gbc);

        // Message Label
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        gbc.gridy = 6;
        add(messageLabel, gbc);

        // Actions
        loginButton.addActionListener(e -> performLogin());
        registerButton.addActionListener(e -> mainFrame.showRegisterPanel());
        forgotButton.addActionListener(e -> showForgotPasswordDialog());
        passwordField.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both email and password.");
            return;
        }

        loginButton.setEnabled(false);
        messageLabel.setText("Authenticating...");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                return userService.login(email, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        if (user instanceof Admin) {
                            mainFrame.showAdminDashboard((Admin) user);
                        } else {
                            mainFrame.showCustomerDashboard((Customer) user);
                        }
                        // Clear fields
                        emailField.setText("");
                        passwordField.setText("");
                        messageLabel.setText("");
                    } else {
                        messageLabel.setText("Invalid email or password.");
                    }
                } catch (Exception e) {
                    messageLabel.setText("Login error: " + e.getMessage());
                } finally {
                    loginButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void showForgotPasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JTextField first = new JTextField();
        JTextField last = new JTextField();
        JTextField email = new JTextField();
        
        panel.add(new JLabel("First Name:")); panel.add(first);
        panel.add(new JLabel("Last Name:")); panel.add(last);
        panel.add(new JLabel("Email:")); panel.add(email);

        int result = JOptionPane.showConfirmDialog(this, panel, "Forgot Password", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            userService.forgotPassword(first.getText(), last.getText(), email.getText());
            JOptionPane.showMessageDialog(this, "If account exists, a temporary password has been sent to your email.");
        }
    }
}
