package com.brandex.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.brandex.models.*;
import com.brandex.services.*;
import com.brandex.datastructures.LinkedList;

public class AdminDashboard extends JPanel {
    private MainFrame mainFrame;
    private Admin admin;
    private ProductService productService;
    private OrderService orderService;
    private UserService userService;
    
    private JTabbedPane tabbedPane;

    public AdminDashboard(MainFrame mainFrame, Admin admin, 
                         ProductService productService, OrderService orderService, UserService userService) {
        this.mainFrame = mainFrame;
        this.admin = admin;
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
        
        setLayout(new BorderLayout());
        createHeader();
        createTabs();
        createFooter();
    }

    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(153, 0, 0)); // Dark red for admin
        header.setPreferredSize(new Dimension(900, 60));
        
        JLabel welcome = new JLabel("  Admin Portal: " + admin.getFirstName());
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Arial", Font.BOLD, 18));
        
        header.add(welcome, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
    }

    private void createTabs() {
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Product Management", createProductManagementPanel());
        tabbedPane.addTab("Order Queue", createOrderQueuePanel());
        tabbedPane.addTab("User Management", createUserManagementPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createProductManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        String[] columns = {"ID", "Name", "Category", "Price", "Stock"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addBtn = new JButton("Add Product");
        JButton removeBtn = new JButton("Remove Selected");
        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(removeBtn);
        panel.add(buttons, BorderLayout.SOUTH);

        Runnable refresh = () -> {
            model.setRowCount(0);
            for (Product p : productService.getAllProducts()) {
                model.addRow(new Object[]{p.getProductId(), p.getName(), p.getCategory(), p.getPrice(), p.getStockQuantity()});
            }
        };
        refresh.run();

        addBtn.addActionListener(e -> {
            // Show a dialog to add product
            JTextField name = new JTextField();
            JTextField cat = new JTextField();
            JTextField price = new JTextField();
            JTextField stock = new JTextField();
            JTextField desc = new JTextField();
            Object[] message = { "Name:", name, "Category:", cat, "Price:", price, "Stock:", stock, "Description:", desc };
            int res = JOptionPane.showConfirmDialog(this, message, "Add Product", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                String id = "P-" + (productService.getAllProducts().size() + 1);
                Product p = new Product(id, name.getText(), cat.getText(), Double.parseDouble(price.getText()), Integer.parseInt(stock.getText()), desc.getText());
                productService.addProduct(p);
                refresh.run();
            }
        });

        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                productService.removeProduct((String) model.getValueAt(row, 0));
                refresh.run();
            }
        });

        return panel;
    }

    private JPanel createOrderQueuePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        String[] columns = {"Order ID", "Customer", "Date", "Status", "Total"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton processBtn = new JButton("Process Next Order");
        JButton processAllBtn = new JButton("Process All Orders");
        JButton manualRefreshBtn = new JButton("Refresh Queue");
        JPanel buttons = new JPanel();
        buttons.add(processBtn);
        buttons.add(processAllBtn);
        buttons.add(manualRefreshBtn);
        panel.add(buttons, BorderLayout.SOUTH);

        Runnable refresh = () -> {
            model.setRowCount(0);
            for (Order o : orderService.getPendingOrders()) {
                model.addRow(new Object[]{o.getOrderId(), o.getCustomerId(), o.getOrderDate(), o.getStatus(), String.format("$%.2f", o.getTotalAmount())});
            }
        };
        refresh.run();

        processBtn.addActionListener(e -> {
            Order o = orderService.processNextOrder();
            if (o != null) {
                JOptionPane.showMessageDialog(this, "Processed Order: " + o.getOrderId());
                refresh.run();
            }
        });

        processAllBtn.addActionListener(e -> {
            orderService.processAllOrders();
            refresh.run();
        });

        manualRefreshBtn.addActionListener(e -> {
            refresh.run();
        });

        // AI Assistance (Gemini CLI): Added this listener to refresh the queue 
        // in real-time when the Admin switches to the "Order Queue" tab.
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) { // Order Queue tab
                refresh.run();
            }
        });

        return panel;
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        String[] columns = {"User ID", "Name", "Email", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        Runnable refresh = () -> {
            model.setRowCount(0);
            for (User u : userService.getAllUsers()) {
                if (!(u instanceof Admin)) {
                    model.addRow(new Object[]{u.getUserId(), u.getFirstName() + " " + u.getLastName(), u.getEmail(), u.getStatus()});
                }
            }
        };
        refresh.run();

        return panel;
    }

    private void createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        footer.add(logoutBtn);
        add(footer, BorderLayout.SOUTH);
        logoutBtn.addActionListener(e -> mainFrame.showLoginPanel());
    }
}
