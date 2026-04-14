package com.brandex.datastructures;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Requirement (b): Product Catalog
 * Requirement (c): Shopping Experience (Cart)
 * We are using a custom Singly Linked List here to store products 
 * and cart items. Linked lists are good for "easy insertion/deletion" 
 * as required in the project description.
 */
public class LinkedList<T> implements Iterable<T> {
    private Node<T> head; // the first item in the list
    private int size;     // keeping track of how many items we have

    public LinkedList() {
        this.head = null; // start empty
        this.size = 0;
    }

    // This adds a new item to the very end of the list
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        
        // If the list is empty, this new node is now the head
        if (head == null) {
            head = newNode;
        } else {
            // Otherwise we walk to the end and attach it
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }
    
    // Quick way to add to the front (useful for Stacks)
    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = head; // point new node to the old head
        head = newNode;      // new node is now the start
        size++;
    }

    // Find and remove a specific piece of data
    public boolean remove(T data) {
        if (head == null) return false;

        // Special case: the data is in the head
        if (head.data.equals(data)) {
            head = head.next;
            size--;
            return true;
        }

        Node<T> current = head;
        Node<T> prev = null;

        // search for the node
        while (current != null && !current.data.equals(data)) {
            prev = current;
            current = current.next;
        }

        // if we found it, skip it in the link chain
        if (current != null) {
            prev.next = current.next;
            size--;
            return true;
        }
        
        return false;
    }
    
    // Remove based on where it is in the list
    public boolean remove(int index) {
        if (index < 0 || index >= size) return false;
        
        if (index == 0) {
            head = head.next;
            size--;
            return true;
        }
        
        Node<T> current = head;
        Node<T> prev = null;
        for (int i = 0; i < index; i++) {
            prev = current;
            current = current.next;
        }
        
        if (current != null) {
            prev.next = current.next;
            size--;
            return true;
        }
        return false;
    }
    
    // Remove from the start of the list - O(1)
    public T removeFirst() {
        if (head == null) return null;
        T data = head.data;
        head = head.next; // second node becomes the new head
        size--;
        return data;
    }

    // Get the data at a certain position
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }
    
    // is the list empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // total items
    public int size() {
        return size;
    }

    // clear everything
    public void clear() {
        head = null;
        size = 0;
    }

    // We implement this so we can use the nice 'for (T item : list)' syntax
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
}

