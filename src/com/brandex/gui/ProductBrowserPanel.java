package com.brandex.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.brandex.services.*;
import com.brandex.models.*;
import com.brandex.datastructures.ArrayList;

public class ProductBrowserPanel extends JPanel {
    private ProductService productService;
    private CartService cartService;
    private Customer customer;
    
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JSpinner quantitySpinner;

    public ProductBrowserPanel(Customer customer, ProductService productService, CartService cartService) {
        this.customer = customer;
        this.productService = productService;
        this.cartService = cartService;
        
        setLayout(new BorderLayout(10, 10));
        createComponents();
        refreshTable();
    }

    private void createComponents() {
        // Search bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        topPanel.add(new JLabel("Search Products:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Category", "Price", "Stock", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(tableModel);
        add(new JScrollPane(productTable), BorderLayout.CENTER);

        // Add to Cart panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JButton addButton = new JButton("Add to Cart");
        bottomPanel.add(new JLabel("Quantity:"));
        bottomPanel.add(quantitySpinner);
        bottomPanel.add(addButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Actions
        searchButton.addActionListener(e -> performSearch());
        addButton.addActionListener(e -> addToCart());
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Product p : productService.getAllProducts()) {
            tableModel.addRow(new Object[]{
                p.getProductId(), p.getName(), p.getCategory(), 
                String.format("$%.2f", p.getPrice()), p.getStockQuantity(), p.getDescription()
            });
        }
    }

    private void performSearch() {
        String name = searchField.getText();
        ArrayList<Product> results = productService.searchByName(name);
        tableModel.setRowCount(0);
        for (Product p : results) {
            tableModel.addRow(new Object[]{
                p.getProductId(), p.getName(), p.getCategory(), 
                String.format("$%.2f", p.getPrice()), p.getStockQuantity(), p.getDescription()
            });
        }
    }

    private void addToCart() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product first.");
            return;
        }
        String productId = (String) tableModel.getValueAt(row, 0);
        Product product = productService.searchById(productId);
        
        // Safety check for null product
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Error: Product could not be retrieved from system.");
            return;
        }
        
        int quantity = (int) quantitySpinner.getValue();
        cartService.addToCart(customer, product, quantity);
        JOptionPane.showMessageDialog(this, "Added to cart!");
    }
}
