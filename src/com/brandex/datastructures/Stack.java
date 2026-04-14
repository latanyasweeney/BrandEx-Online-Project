package com.brandex.datastructures;

/**
 * Requirement (c): Shopping Experience (Undo/Redo)
 * This is our Stack implementation using a Linked List.
 * It follows the LIFO (Last-In, First-Out) principle.
 * We use this to keep track of what the user does in their cart so 
 * they can undo or redo stuff like adding an item or changing the quantity.
 */
public class Stack<T> {
    private LinkedList<T> list;

    public Stack() {
        this.list = new LinkedList<>(); // initialize our list
    }

    // Push adds a new item to the top of the stack
    public void push(T item) {
        list.addFirst(item); // we add to the front for O(1) speed
    }

    // Pop takes the top item off the stack and returns it
    public T pop() {
        if (isEmpty()) {
            return null; 
        }
        return list.removeFirst();
    }

    // Peek lets us see what's on top without removing it
    public T peek() {
        if (isEmpty()) return null;
        return list.get(0);
    }

    // Check if the stack is empty
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    // How many items are in the stack?
    public int size() {
        return list.size();
    }
    
    // Empty the whole stack
    public void clear() {
        list.clear();
    }
}

