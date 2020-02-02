package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IPriorityQueue;

import java.util.Iterator;

public class MinFourHeap<E> implements IPriorityQueue<E> {

    private static final int DEFAULT_CAPACITY = 5;

    private int size;
    private PQElement<E>[] data;
    private IDictionary<E, Integer> keyToIndexMap;

    /**
     * Creates a new empty heap with DEFAULT_CAPACITY.
     */
    public MinFourHeap() {
        this.size = 0;
        this.data = new PQElement[DEFAULT_CAPACITY];
        this.keyToIndexMap = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
    }

    private int getSmallestChild(int index) {
        int minIndex = 4*index + 1;

        if (minIndex < size) {
            for (int i = 2; i <= 4; i++) {
                if (4 * index + i < size && data[4 * index + 1] != null && data[minIndex] != null && data[4 * index + i].priority < data[minIndex].priority) {
                    minIndex = 4 * index + i;
                }
            }
        }
        return minIndex;

    }

    @Override
    public void increaseKey(PQElement<E> key) {
        if (key != null && keyToIndexMap.containsKey(key.data)) {
            int index = keyToIndexMap.get(key.data);
            data[index] = key;
            keyToIndexMap.put(key.data, index);

            int currentIndex = keyToIndexMap.get(key.data);
            int childIndex = getSmallestChild(currentIndex);
            while (childIndex < this.size && data[childIndex] != null && data[childIndex].priority < data[currentIndex].priority){
                PQElement<E> parent = data[currentIndex];
                PQElement<E> child = data[childIndex];
                keyToIndexMap.put(parent.data, childIndex);
                keyToIndexMap.put(child.data, currentIndex);

                data[childIndex] = parent;
                data[currentIndex] = child;
                currentIndex = childIndex;
                childIndex = getSmallestChild(currentIndex);
            }
        } else if (key != null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void decreaseKey(PQElement<E> key) {
        if (key != null && keyToIndexMap.containsKey(key.data)) {
            int index = keyToIndexMap.get(key.data);
            data[index] = key;
            keyToIndexMap.put(key.data, index);

            int currentIndex = keyToIndexMap.get(key.data);
            int parentIndex = (currentIndex - 1) / 4;

            while (currentIndex > 0 && data[currentIndex].priority < data[parentIndex].priority) {
                PQElement<E> parent = data[parentIndex];
                PQElement<E> child = data[currentIndex];
                keyToIndexMap.put(parent.data, currentIndex);
                keyToIndexMap.put(child.data, parentIndex);

                data[currentIndex] = parent;
                data[parentIndex] = child;
                currentIndex = parentIndex;
                parentIndex = (currentIndex - 1) / 4;
            }
        } else if (key != null) {
            throw new IllegalArgumentException();
        }
    }

    private void resize(){
        PQElement<E>[] newData = new PQElement[this.data.length * 2];
        for (int i = 0; i < data.length; i++) {
            newData[i] = data[i];
        }
        this.data = newData;
    }

    @Override
    public boolean enqueue(PQElement<E> epqElement) {
        if (keyToIndexMap.containsKey(epqElement.data)) {
            throw new IllegalArgumentException("Duplicate value");
        }

        if (this.size == data.length) {
            resize();
        }

        this.size++;
        data[this.size - 1] = epqElement;
        keyToIndexMap.put(epqElement.data, this.size - 1);

        decreaseKey(epqElement);

        return true;
    }

    @Override
    public PQElement<E> dequeue() {
        if (size == 0) {
            throw new IndexOutOfBoundsException();
        }

        PQElement min = data[0];
        data[0] = data[this.size - 1];
        keyToIndexMap.put(data[0].data, 0);
        data[this.size - 1] = null;
        size--;
        increaseKey(data[0]);

        return min;
    }

    @Override
    public PQElement<E> peek() {
        if (size == 0) {
            throw new IndexOutOfBoundsException();
        }
       return data[0];
    }

    @Override
    public int size() {
        return size;
    }

    private class SASIterator implements Iterator<PQElement<E>> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < MinFourHeap.this.size;
        }

        @Override
        public PQElement<E> next() {
            PQElement<E> result = MinFourHeap.this.data[this.currentIndex];
            this.currentIndex++;
            return result;
        }
    }

    @Override
    public Iterator<PQElement<E>> iterator() {
        return new SASIterator();
    }
}