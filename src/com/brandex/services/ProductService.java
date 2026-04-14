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
    private LinkedList<Product> productList;

    public ProductService() {
        this.productList = new LinkedList<>();
        this.productBST = new BinarySearchTree<>();
        loadProductsFromFile();
    }

    public void addProduct(Product p) {
        productList.add(p);
        productBST.insert(p);
        FileHandler.saveProducts(productList);
    }
    
    public boolean removeProduct(String productId) {
        Product dummy = new Product(productId, "", "", 0, 0, "");
        Product found = productBST.search(dummy);
        if (found != null) {
            productBST.delete(found);
            // We need to remove from productList too. 
            // LinkedList.remove(T data) needs to be correctly implemented or we use index.
            int index = 0;
            for (Product p : productList) {
                if (p.getProductId().equals(productId)) {
                    productList.remove(index);
                    break;
                }
                index++;
            }
            FileHandler.saveProducts(productList);
            return true;
        }
        return false;
    }

    public Product searchById(String id) {
        // 1. Try BST search first (for speed)
        Product searchKey = new Product(id, "", "", 0, 0, "");
        Product p = productBST.search(searchKey);
        
        // 2. Fallback: Search the LinkedList if BST fails
        if (p == null) {
            for (Product item : productList) {
                if (item.getProductId().equals(id)) {
                    return item;
                }
            }
        }
        return p;
    }

    public ArrayList<Product> searchByName(String name) {
        ArrayList<Product> results = new ArrayList<>();
        ArrayList<Product> all = productBST.toList();
        for (Product p : all) {
            if (p.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }

    public LinkedList<Product> getAllProducts() {
        return productList;
    }
    
    public void loadProductsFromFile() {
        this.productList = FileHandler.loadProducts();
        this.productBST = new BinarySearchTree<>();
        for (Product p : productList) {
            productBST.insert(p);
        }
    }

    public BinarySearchTree<Product> getBST() {
        return productBST;
    }
}
