package com.brandex.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.brandex.services.*;
import com.brandex.models.*;

public class CartPanel extends JPanel {
    private CartService cartService;
    private OrderService orderService;
    private Customer customer;
    
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;

    public CartPanel(Customer customer, CartService cartService, OrderService orderService) {
        this.customer = customer;
        this.cartService = cartService;
        this.orderService = orderService;
        
        setLayout(new BorderLayout(10, 10));
        createComponents();
        refreshCart();
    }

    private void createComponents() {
        // Table
        String[] columns = {"Product", "Price", "Qty", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(tableModel);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton undoBtn = new JButton("Undo");
        JButton redoBtn = new JButton("Redo");
        JButton removeBtn = new JButton("Remove Selected");
        JButton clearBtn = new JButton("Clear Cart");
        buttonsPanel.add(undoBtn);
        buttonsPanel.add(redoBtn);
        buttonsPanel.add(removeBtn);
        buttonsPanel.add(clearBtn);
        
        JPanel checkoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.setBackground(new Color(0, 153, 0));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutPanel.add(totalLabel);
        checkoutPanel.add(checkoutBtn);
        
        controlPanel.add(buttonsPanel, BorderLayout.WEST);
        controlPanel.add(checkoutPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);

        // Actions
        undoBtn.addActionListener(e -> { cartService.undo(customer); refreshCart(); });
        redoBtn.addActionListener(e -> { cartService.redo(customer); refreshCart(); });
        removeBtn.addActionListener(e -> removeSelected());
        clearBtn.addActionListener(e -> { cartService.clearCart(customer); refreshCart(); });
        checkoutBtn.addActionListener(e -> performCheckout());
    }

    public void refreshCart() {
        tableModel.setRowCount(0);
        for (CartItem item : customer.getCart()) {
            tableModel.addRow(new Object[]{
                item.getProduct().getName(),
                String.format("$%.2f", item.getProduct().getPrice()),
                item.getQuantity(),
                String.format("$%.2f", item.getTotalPrice())
            });
        }
        totalLabel.setText(String.format("Total: $%.2f", cartService.getCartTotal(customer)));
    }

    private void removeSelected() {
        int row = cartTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item to remove.");
            return;
        }
        // In our tableModel, we don't have ID. Let's get it from the cart list by index.
        CartItem item = customer.getCart().get(row);
        cartService.removeFromCart(customer, item.getProduct().getProductId());
        refreshCart();
    }

    private void performCheckout() {
        if (customer.getCart().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        String address = JOptionPane.showInputDialog(this, "Enter Shipping Address:");
        if (address != null && !address.trim().isEmpty()) {
            Order order = orderService.createOrder(customer, address);
            if (order != null) {
                JOptionPane.showMessageDialog(this, "Order placed! Order ID: " + order.getOrderId());
                cartService.clearCart(customer);
                refreshCart();
            }
        }
    }
}
