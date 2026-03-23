package com.brandex.services;

import com.brandex.models.Product;
import com.brandex.datastructures.BinarySearchTree;
import com.brandex.datastructures.LinkedList;
import com.brandex.datastructures.ArrayList;
import com.brandex.utils.FileHandler;

/**
 * Manages products using a Binary Search Tree for efficient ID lookup.
 */
public class ProductService {
    private BinarySearchTree<Product> productBST;
    private LinkedList<Product> productList; // Keep a list for easy iteration too

    public ProductService() {
        this.productList = FileHandler.loadProducts();
        this.productBST = new BinarySearchTree<>();
        
        // Populate BST
        for (Product p : productList) {
            productBST.insert(p);
        }
    }

    public void addProduct(String name, String category, double price, int stock, String description) {
        String id = "P" + (1000 + productList.size());
        Product newProduct = new Product(id, name, category, price, stock, description);
        
        productList.add(newProduct);
        productBST.insert(newProduct);
        FileHandler.saveProducts(productList);
        System.out.println("Product added: " + id);
    }
    
    public boolean removeProduct(String id) {
        Product p = searchById(id);
        if (p != null) {
            productBST.delete(p);
            productList.remove(p);
            FileHandler.saveProducts(productList);
            return true;
        }
        return false;
    }

    public Product searchById(String id) {
        // Create dummy product for search
        Product searchKey = new Product(id, "", "", 0, 0, "");
        return productBST.search(searchKey);
    }

    public void searchByName(String partialName) {
        System.out.println("Search Results for '" + partialName + "':");
        // Use in-order traversal to get all products sorted by ID
        // Note: For true name search, iterating the list is O(n) which is fine.
        // But requirement asks to use in-order traversal logic.
        
        ArrayList<Product> allProducts = productBST.toList(); // Get sorted list from BST
        boolean found = false;
        
        for (Product p : allProducts) {
            if (p.getName().toLowerCase().contains(partialName.toLowerCase())) {
                System.out.println(p);
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("No products found.");
        }
    }

    public LinkedList<Product> getAllProducts() {
        return productList;
    }
    
    public BinarySearchTree<Product> getBST() {
        return productBST;
    }
}
