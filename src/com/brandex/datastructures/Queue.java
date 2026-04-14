package com.brandex.datastructures;

import java.util.Iterator;

/**
 * Requirement (d): Order Processing
 * We use a Queue here for handling customer orders.
 * It works like a real-life line - FIFO (First-In, First-Out).
 * This makes sure that the warehouse processes orders in the right order.
 */
public class Queue<T> implements Iterable<T> {
    private LinkedList<T> list;

    public Queue() {
        this.list = new LinkedList<>(); // use our linked list to hold the items
    }

    // Enqueue adds a new order to the back of the line
    public void enqueue(T item) {
        list.add(item);
    }

    // Dequeue takes the order at the front of the line and removes it
    public T dequeue() {
        if (isEmpty()) return null;
        return list.removeFirst(); // FIFO removal
    }

    // Peek lets us see who's first in line without removing them
    public T peek() {
        if (isEmpty()) return null;
        return list.get(0);
    }

    // is the queue empty?
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    // total number of orders in line
    public int size() {
        return list.size();
    }

    // empty out the queue
    public void clear() {
        list.clear();
    }

    // We can iterate through the queue to show all pending orders 
    // without actually dequeuing them.
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}

