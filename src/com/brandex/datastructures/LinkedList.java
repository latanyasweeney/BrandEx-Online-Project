package com.brandex.datastructures;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Custom LinkedList implementation.
 * This is a singly linked list where each node points to the next one.
 * We implement Iterable so we can use for-each loops easily.
 */
public class LinkedList<T> implements Iterable<T> {
    private Node<T> head;
    private int size;

    public LinkedList() {
        this.head = null;
        this.size = 0;
    }

    // Add element to the end of the list - O(n)
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        
        // If list is empty, new node becomes head
        if (head == null) {
            head = newNode;
        } else {
            // Traverse to the end
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }
    
    // Add element to the beginning - O(1)
    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = head;
        head = newNode;
        size++;
    }

    // Remove first occurrence of data - O(n)
    public boolean remove(T data) {
        if (head == null) return false;

        // If head holds the data
        if (head.data.equals(data)) {
            head = head.next;
            size--;
            return true;
        }

        Node<T> current = head;
        Node<T> prev = null;

        while (current != null && !current.data.equals(data)) {
            prev = current;
            current = current.next;
        }

        // If found
        if (current != null) {
            prev.next = current.next;
            size--;
            return true;
        }
        
        return false;
    }
    
    // Remove from front (for Queue/Stack) - O(1)
    public T removeFirst() {
        if (head == null) return null;
        T data = head.data;
        head = head.next;
        size--;
        return data;
    }

    // Get element at index - O(n)
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
    
    // Check if list is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Get size
    public int size() {
        return size;
    }

    // Clear the list
    public void clear() {
        head = null;
        size = 0;
    }

    // Implementing iterator for for-each loop support
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
