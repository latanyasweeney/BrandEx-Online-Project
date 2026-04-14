package com.brandex.gui;

import javax.swing.*;
import java.awt.*;
import com.brandex.services.UserService;
import com.brandex.models.Customer;

public class RegisterPanel extends JPanel {
    private MainFrame mainFrame;
    private UserService userService;
    
    private JTextField firstNameField, lastNameField, emailField;
    private JButton registerButton, backButton;
    private JLabel messageLabel;

    public RegisterPanel(MainFrame mainFrame, UserService userService) {
        this.mainFrame = mainFrame;
        this.userService = userService;
        
        setLayout(new GridBagLayout());
        setBackground(new Color(230, 240, 255));
        
        createComponents();
    }

    private void createComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Join BrandEx", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 128, 128));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Fields
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("First Name:"), gbc);
        firstNameField = new JTextField(20); gbc.gridx = 1; add(firstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Last Name:"), gbc);
        lastNameField = new JTextField(20); gbc.gridx = 1; add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20); gbc.gridx = 1; add(emailField, gbc);

        // Register Button
        registerButton = new JButton("Register");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(registerButton, gbc);

        // Back Button
        backButton = new JButton("Back to Login");
        gbc.gridy = 5;
        add(backButton, gbc);

        // Message Label
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        gbc.gridy = 6;
        add(messageLabel, gbc);

        // Actions
        registerButton.addActionListener(e -> handleRegister());
        backButton.addActionListener(e -> mainFrame.showLoginPanel());
    }

    private void handleRegister() {
        String first = firstNameField.getText();
        String last = lastNameField.getText();
        String email = emailField.getText();

        if (first.isEmpty() || last.isEmpty() || email.isEmpty()) {
            messageLabel.setText("All fields are required.");
            return;
        }

        Customer customer = userService.register(first, last, email);
        if (customer != null) {
            showOTPDialog(customer);
        } else {
            messageLabel.setText("Registration failed (invalid input or email exists).");
        }
    }

    private void showOTPDialog(Customer customer) {
        String enteredOtp = JOptionPane.showInputDialog(this, "Enter the OTP sent to your email:");
        if (enteredOtp != null) {
            if (userService.verifyOTP(customer, enteredOtp)) {
                forcePasswordChangeDialog(customer);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid OTP. Please try registering again.", "Error", JOptionPane.ERROR_MESSAGE);
                mainFrame.showLoginPanel();
            }
        }
    }

    private void forcePasswordChangeDialog(Customer customer) {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        JPasswordField pass1 = new JPasswordField();
        JPasswordField pass2 = new JPasswordField();
        
        panel.add(new JLabel("New Password:")); panel.add(pass1);
        panel.add(new JLabel("Confirm Password:")); panel.add(pass2);

        int result = JOptionPane.showConfirmDialog(this, panel, "Set Password", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String p1 = new String(pass1.getPassword());
            String p2 = new String(pass2.getPassword());
            
            if (p1.equals(p2) && !p1.isEmpty()) {
                userService.forceSetPassword(customer, p1);
                JOptionPane.showMessageDialog(this, "Password set successfully! Please login.");
                mainFrame.showLoginPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Passwords do not match or are empty.", "Error", JOptionPane.ERROR_MESSAGE);
                forcePasswordChangeDialog(customer);
            }
        }
    }
}
