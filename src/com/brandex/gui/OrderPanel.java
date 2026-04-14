package com.brandex.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.brandex.models.*;

public class OrderPanel extends JPanel {
    private Customer customer;
    private JTable orderTable;
    private DefaultTableModel tableModel;

    public OrderPanel(Customer customer) {
        this.customer = customer;
        setLayout(new BorderLayout(10, 10));
        createComponents();
        refreshOrders();
    }

    private void createComponents() {
        String[] columns = {"Order ID", "Date", "Status", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        orderTable = new JTable(tableModel);
        add(new JScrollPane(orderTable), BorderLayout.CENTER);

        JButton viewDetailsBtn = new JButton("View Order Details");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(viewDetailsBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        viewDetailsBtn.addActionListener(e -> showDetails());
    }

    public void refreshOrders() {
        tableModel.setRowCount(0);
        for (Order order : customer.getOrderHistory()) {
            tableModel.addRow(new Object[]{
                order.getOrderId(), order.getOrderDate(), order.getStatus(), 
                String.format("$%.2f", order.getTotalAmount())
            });
        }
    }

    private void showDetails() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order to view details.");
            return;
        }
        Order order = customer.getOrderHistory().get(row);
        
        StringBuilder details = new StringBuilder();
        details.append("Order: ").append(order.getOrderId()).append("\n");
        details.append("Date: ").append(order.getOrderDate()).append("\n");
        details.append("Shipping Address: ").append(order.getShippingAddress()).append("\n\n");
        details.append("Items:\n");
        for (CartItem item : order.getItems()) {
            details.append("- ").append(item).append("\n");
        }
        details.append("\nTotal: $").append(String.format("%.2f", order.getTotalAmount()));
        
        JOptionPane.showMessageDialog(this, new JScrollPane(new JTextArea(details.toString(), 15, 30)), "Order Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
