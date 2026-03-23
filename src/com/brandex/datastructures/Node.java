package com.brandex.datastructures;

/**
 * A generic Node class for our LinkedList implementation.
 * It holds data of type T and a reference to the next node.
 */
public class Node<T> {
    public T data;
    public Node<T> next;

    // Constructor to create a new node
    public Node(T data) {
        this.data = data;
        this.next = null;
    }
}
