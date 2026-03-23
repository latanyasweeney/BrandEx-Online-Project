package com.brandex.datastructures;

/**
 *  Binary Search Tree implementation.
 * Used for fast lookups (O(log n) average case).
 * Elements must implement Comparable interface.
 */
public class BinarySearchTree<T extends Comparable<T>> {
    private TreeNode<T> root;

    public BinarySearchTree() {
        this.root = null;
    }

    // Public insert method
    public void insert(T data) {
        root = insertRec(root, data);
    }

    // Recursive insert helper
    private TreeNode<T> insertRec(TreeNode<T> root, T data) {
        // Base case: If the tree is empty, return a new node
        if (root == null) {
            root = new TreeNode<>(data);
            return root;
        }

        // Otherwise, recur down the tree
        if (data.compareTo(root.data) < 0) {
            root.left = insertRec(root.left, data);
        } else if (data.compareTo(root.data) > 0) {
            root.right = insertRec(root.right, data);
        }

        // Return the (unchanged) node pointer
        return root;
    }

    // Public search method
    public T search(T data) {
        TreeNode<T> result = searchRec(root, data);
        if (result != null) {
            return result.data;
        }
        return null;
    }

    // Recursive search helper
    private TreeNode<T> searchRec(TreeNode<T> root, T data) {
        // Base Cases: root is null or data is present at root
        if (root == null || root.data.equals(data)) // utilizing equals for precise match if compareTo is 0
            return root;

        // Data is smaller than root's data
        if (data.compareTo(root.data) < 0)
            return searchRec(root.left, data);

        // Data is greater than root's data
        return searchRec(root.right, data);
    }
    
    // Delete method
    public void delete(T data) {
        root = deleteRec(root, data);
    }

    private TreeNode<T> deleteRec(TreeNode<T> root, T data) {
        if (root == null) return root;

        if (data.compareTo(root.data) < 0)
            root.left = deleteRec(root.left, data);
        else if (data.compareTo(root.data) > 0)
            root.right = deleteRec(root.right, data);
        else {
            // Node with only one child or no child
            if (root.left == null)
                return root.right;
            else if (root.right == null)
                return root.left;

            // Node with two children: Get the inorder successor (smallest in the right subtree)
            root.data = minValue(root.right);

            // Delete the inorder successor
            root.right = deleteRec(root.right, root.data);
        }
        return root;
    }

    private T minValue(TreeNode<T> root) {
        T minv = root.data;
        while (root.left != null) {
            minv = root.left.data;
            root = root.left;
        }
        return minv;
    }

    // Convert tree to ArrayList using in-order traversal
    public ArrayList<T> toList() {
        ArrayList<T> list = new ArrayList<>();
        toListRec(root, list);
        return list;
    }

    private void toListRec(TreeNode<T> root, ArrayList<T> list) {
        if (root != null) {
            toListRec(root.left, list);
            list.add(root.data);
            toListRec(root.right, list);
        }
    }

    // In-order traversal (Left -> Root -> Right)
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
