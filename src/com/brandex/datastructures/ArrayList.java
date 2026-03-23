package com.brandex.datastructures;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Custom dynamic array implementation (like Java's ArrayList).
 * Automatically resizes when full.
 */
public class ArrayList<T> implements Iterable<T> {
    private Object[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public ArrayList() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    // Add element to the end - Amortized O(1)
    public void add(T item) {
        if (size == elements.length) {
            resize();
        }
        elements[size++] = item;
    }

    // Double the capacity when full
    private void resize() {
        int newCapacity = elements.length * 2;
        Object[] newElements = new Object[newCapacity];
        
        // Copy elements manually
        for (int i = 0; i < size; i++) {
            newElements[i] = elements[i];
        }
        
        elements = newElements;
    }

    // Get element at index - O(1)
    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (T) elements[index];
    }
    
    // Remove element at index
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        // Shift elements to the left
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[size - 1] = null; // Help garbage collection
        size--;
    }

    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (T) elements[currentIndex++];
            }
        };
    }
}
