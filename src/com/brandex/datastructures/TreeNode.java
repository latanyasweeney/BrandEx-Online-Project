package com.brandex.datastructures;

/**
 * Node for Binary Search Tree.
 * Contains data and references to left and right children.
 */
public class TreeNode<T extends Comparable<T>> {
    public T data;
    public TreeNode<T> left;
    public TreeNode<T> right;

    public TreeNode(T data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}
