package com.brandex.datastructures;

/**
 * Requirement (b): Product Catalog Search
 * We are using a Binary Search Tree (BST) here because it makes searching 
 * for products by ID or Name really fast (O(log n)).
 * This helps us meet the requirement to "quickly locate products".
 */
public class BinarySearchTree<T extends Comparable<T>> {
    private TreeNode<T> root;

    public BinarySearchTree() {
        this.root = null; // starting with an empty tree
    }

    // This method lets us add a new item into the BST
    public void insert(T data) {
        root = insertRec(root, data);
    }

    // This is a helper method that uses recursion to find where to put the new data
    private TreeNode<T> insertRec(TreeNode<T> root, T data) {
        // If we found an empty spot, put the new node here
        if (root == null) {
            root = new TreeNode<>(data);
            return root;
        }

        // If the data is smaller, go left. If it's bigger, go right.
        if (data.compareTo(root.data) < 0) {
            root.left = insertRec(root.left, data);
        } else if (data.compareTo(root.data) > 0) {
            root.right = insertRec(root.right, data);
        }

        return root;
    }

    // This is the search function for requirement (b)
    public T search(T data) {
        TreeNode<T> result = searchRec(root, data);
        if (result != null) {
            return result.data;
        }
        return null; // nothing found
    }

    // Using recursion to search through the tree
    private TreeNode<T> searchRec(TreeNode<T> root, T data) {
        // If we hit a null or find the data, we're done
        if (root == null || root.data.equals(data)) 
            return root;

        // Smaller? go left.
        if (data.compareTo(root.data) < 0)
            return searchRec(root.left, data);

        // Bigger? go right.
        return searchRec(root.right, data);
    }
    
    // Method to remove a node from the tree
    public void delete(T data) {
        root = deleteRec(root, data);
    }

    private TreeNode<T> deleteRec(TreeNode<T> root, T data) {
        if (root == null) return root;

        // standard BST delete logic
        if (data.compareTo(root.data) < 0)
            root.left = deleteRec(root.left, data);
        else if (data.compareTo(root.data) > 0)
            root.right = deleteRec(root.right, root.data);
        else {
            // Found the node to delete!
            
            // Case 1: only one child or none
            if (root.left == null)
                return root.right;
            else if (root.right == null)
                return root.left;

            // Case 2: two children - need to find the smallest on the right side
            root.data = minValue(root.right);

            // delete that successor
            root.right = deleteRec(root.right, root.data);
        }
        return root;
    }

    // Helper to find the minimum value in a subtree
    private T minValue(TreeNode<T> root) {
        T minv = root.data;
        while (root.left != null) {
            minv = root.left.data;
            root = root.left;
        }
        return minv;
    }

    // Turns the tree into a list so we can show it in the UI more easily
    public ArrayList<T> toList() {
        ArrayList<T> list = new ArrayList<>();
        toListRec(root, list);
        return list;
    }

    private void toListRec(TreeNode<T> root, ArrayList<T> list) {
        if (root != null) {
            toListRec(root.left, list); // go left
            list.add(root.data);        // add self
            toListRec(root.right, list); // go right
        }
    }

    // prints the tree in order (for debugging)
    public void inOrder() {
        inOrderRec(root);
        System.out.println();
    }

    private void inOrderRec(TreeNode<T> root) {
        if (root != null) {
            inOrderRec(root.left);
            System.out.print(root.data + " ");
            inOrderRec(root.right);
        }
    }
}

