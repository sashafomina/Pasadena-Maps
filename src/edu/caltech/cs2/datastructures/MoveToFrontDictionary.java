package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.ISet;

import java.util.Iterator;

public class MoveToFrontDictionary<K, V> implements IDictionary<K,V> {
    private int size;
    private Node<K,V> head;

    private class Node<K,V> {
        private K key;
        private V value;
        private Node<K,V> next;
        private Node<K,V> prev;

        private Node(){
            this(null, null);
        }

        private Node(K key, V value){
            this.key = key;
            this.value = value;
            this.next = null;
            this.prev = null;
        }
    }

    private class MDIterator<K> implements Iterator<K>{
        private int currentIndex = 0;
        private MoveToFrontDictionary.Node nextNode = MoveToFrontDictionary.this.head;

        public boolean hasNext() {
            return nextNode != null;
        }

        public K next(){
            K result = (K) nextNode.key;
            this.currentIndex++;
            nextNode = nextNode.next;
            return result;
        }
    }

    public MoveToFrontDictionary(){
        this.head = null;
        this.size = 0;
    }

    @Override
    public V get(K key) {
        if(!containsKey(key)) {
            return null;
        }
        Node<K,V> curr = this.head;
        if (this.head.next == null){
            return this.head.value;
        }
        V desiredVal = null;
        while (curr != null){
            if (curr.key.equals(key)){
                return curr.value;
            }
            curr = curr.next;
        }
        return null;
    }

    @Override
    public V remove(K key) {
        V removed = null;
        Node<K,V> curr = this.head;
        if (!containsKey(key)){
            return null;
        }
        else if (curr.next == null && curr.prev == null){
            removed = this.head.value;
            this.head = null;
            this.size--;
            return removed;
        }

        while (curr != null){
            if (curr.key.equals(key)){
                removed = curr.value;
                if (curr == this.head){
                    this.head = curr.next;
                }
                if (curr.prev != null) {
                    curr.prev.next = curr.next;
                }
                if (curr.next != null){
                    curr.next.prev = curr.prev;
                }

                //System.out.println(containsKey(key));
                this.size--;
                return removed;
            }
            curr = curr.next;
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (this.head == null){
            this.head = new Node(key, value);
            this.size++;
            return null;
        }
        if (!containsKey(key)) {
            Node<K, V> newNode = new Node(key, value);
            newNode.next = this.head;
            this.head.prev = newNode;
            newNode.prev = null;
            this.head = newNode;
            this.size++;
            return null;
        }
        else{
            Node<K, V> curr = this.head;
            V oldValue = null;
            while (curr != null){
                if (curr.key.equals(key)){
                    oldValue = curr.value;
                    if (curr == this.head){
                        curr.value = value;
                        return oldValue;
                    }
                    break;
                }
                curr = curr.next;
            }
            return oldValue;
        }
    }

    @Override
    public boolean containsKey(K key) {
        Node<K, V> curr = this.head;
        while (curr != null){
            if (curr.key.equals(key)){
                if (curr.prev == null) {
                    return true;
                }

                if (curr.next != null){
                    curr.next.prev = curr.prev;
                }

                curr.prev.next = curr.next;
                curr.prev = null;
                this.head.prev = curr;
                curr.next = this.head;
                this.head = curr;

                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        Node<K, V> curr = this.head;
        while (curr != null){
            if (curr.value.equals(value)){
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        ICollection<K> keySet = new LinkedDeque<>();
        Node<K,V> curr = this.head;
        while (curr != null){
            keySet.add(curr.key);
            curr = curr.next;
        }
        return keySet;
    }

    @Override
    public ICollection<V> values() {
        ICollection<V> values = new LinkedDeque<>();
        Node<K,V> curr = this.head;
        while (curr != null){
            values.add(curr.value);
            curr = curr.next;
        }
        return values;
    }

    @Override
    public Iterator<K> iterator() {
        return new MDIterator<>();

    }
}
