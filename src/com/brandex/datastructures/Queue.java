package com.brandex.datastructures;

import java.util.Iterator;

/**
 * A generic Queue implementation using our LinkedList.
 * Follows FIFO (First-In, First-Out) principle.
 */
public class Queue<T> implements Iterable<T> {
    private LinkedList<T> list;

    public Queue() {
        this.list = new LinkedList<>();
    }

    // Enqueue adds to the back (end of list) - O(n) for singly linked list without tail pointer
    public void enqueue(T item) {
        list.add(item);
    }

    // Dequeue removes from the front - O(1)
    public T dequeue() {
        if (isEmpty()) return null;
        return list.removeFirst();
    }

    // Peek looks at the front without removing
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

    // Allow iterating without dequeuing (useful for viewing the queue)
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
