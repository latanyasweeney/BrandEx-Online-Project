package com.brandex.datastructures;

/**
 * A generic Stack implementation using our LinkedList.
 * Follows LIFO (Last-In, First-Out) principle.
 */
public class Stack<T> {
    private LinkedList<T> list;

    public Stack() {
        this.list = new LinkedList<>();
    }

    // Push adds to the top (front of list) - O(1)
    public void push(T item) {
        list.addFirst(item);
    }

    // Pop removes from the top - O(1)
    public T pop() {
        if (isEmpty()) {
            return null; // Or throw exception
        }
        return list.removeFirst();
    }

    // Peek looks at the top without removing - O(1)
    public T peek() {
        if (isEmpty()) return null;
        return list.get(0);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    public int size() {
        return list.size();
    }
    
    public void clear() {
        list.clear();
    }
}
