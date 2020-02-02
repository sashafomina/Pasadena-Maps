package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {

    private E[] deque;
    private int end;
    private static final int defaultCapacity = 10;
    private static final int growFactor = 2;

    public ArrayDeque() {
        this.end = -1;
        this.deque = (E[]) new Object[defaultCapacity];
    }

    public ArrayDeque(int initialCapacity) {
        this.end = -1;
        this.deque = (E[]) new Object[initialCapacity];
    }

    @Override
    public void addFront(E e) {
        E[] newDeque = (E[]) new Object[deque.length];
        if (end >= deque.length - 1) {
            newDeque = (E[]) new Object[deque.length * growFactor];
        }

        for (int ii = 1; ii <= end + 1; ii++) {
            newDeque[ii] = (E) deque[ii - 1];
        }
        newDeque[0] = e;

        deque = newDeque.clone();
        end++;
    }

    @Override
    public void addBack(E e) {
        if (end < deque.length - 1) {
            deque[end + 1] = e;
            end++;
        } else {
            E[] newDeque = (E[]) new Object[deque.length * growFactor];

            for (int ii = 0; ii < deque.length; ii++) {
                newDeque[ii] = (E) deque[ii];
            }
            newDeque[end + 1] = e;
            end++;
            deque = newDeque.clone();
        }
    }

    @Override
    public E removeFront() {
        if (end < 0) {
            return null;
        }

        E removed = (E) deque[0];

        for (int ii = 0; ii < deque.length - 1; ii++) {
            deque[ii] = deque[ii + 1];
        }

        end--;
        return removed;
    }

    @Override
    public E removeBack() {
        if (end < 0) {
            return null;
        }

        E removed = (E) deque[end];
        deque[end] = null;

        end--;
        return removed;
    }

    @Override
    public boolean enqueue(E e) {
        addFront(e);
        return true;
    }

    @Override
    public E dequeue() {
        return removeBack();
    }

    @Override
    public boolean push(E e) {
        addBack(e);
        return true;
    }

    @Override
    public E pop() {
        return removeBack();
    }

    @Override
    public E peek() {
        return peekBack();
    }

    @Override
    public E peekFront() {
        return (E) deque[0];
    }

    @Override
    public E peekBack() {
        if (end >= 0) {
            return (E) deque[end];
        }
        return null;
    }

    private class SASIterator implements Iterator<E> {

        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < ArrayDeque.this.size();
        }

        @Override
        public E next() {
            E result = (E) ArrayDeque.this.deque[this.currentIndex];
            this.currentIndex++;
            return result;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new SASIterator();
    }

    @Override
    public int size() {
        return end + 1;
    }

    @Override
    public String toString() {
        if (deque[0] == null) {
            return "[]";
        }

        int ii = 0;
        String result = "";
        while (ii <= end) {
            result += deque[ii].toString() + ", ";
            ii++;
        }

        return "[" + result.substring(0, result.length() - 2) + "]";
    }
}
