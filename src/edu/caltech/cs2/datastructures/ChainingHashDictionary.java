package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;
import java.util.function.Supplier;


public class ChainingHashDictionary<K, V> implements IDictionary<K, V> {
    private Supplier<IDictionary<K, V>> chain;
    private IDictionary<K,V>[] hashTable;
    private int size;
    private int pindex;
    private static final int[] primes = {2,5,11,23,47,97,197,397,797,1597,3203,6421,12853,25717,51437,102877, 205759, 411527};

    private class CHIterator<K> implements Iterator<K> {
        private int currentSize = 0;
        private Iterator<K> keySet = null;
        private int chainSize = ChainingHashDictionary.this.size();

        public boolean hasNext() {
            return currentSize < chainSize;
        }

        public K next() {
            if (this.currentSize == 0){
                this.keySet = ((ICollection<K>) ChainingHashDictionary.this.keys()).iterator();
            }
            K next = this.keySet.next();
            this.currentSize++;
            return next;
        }

    }

    public ChainingHashDictionary(Supplier<IDictionary<K,V>> chain){
        this.size = 0;
        this.chain = chain;
        this.pindex = 0;
        this.hashTable = new IDictionary[primes[pindex]];
    }


    @Override
    public V get(K key) {
        if (containsKey(key)) {
            return this.hashTable[Math.abs(key.hashCode() % hashTable.length)].get(key);
        }
        return null;
    }

//    @Override
//    public V remove(K key) {
//        V old = null;
//        if (!this.containsKey(key)){
//            return null;
//        }
//        for (int i = 0; i < hashTable.length; i++){
//            if (hashTable[i] != null && hashTable[i].containsKey(key)){
//                old = hashTable[i].get(key);
//                hashTable[i].remove(key);
//                this.size--;
//                break;
//            }
//        }
//        return old;
//    }

    public V remove(K key) {
        if (containsKey(key)) {
            size--;
        }
        int index = Math.abs(key.hashCode() % hashTable.length);
        return hashTable[index].remove(key);
    }

    private void expand(){
        this.pindex++;
        IDictionary<K,V>[] oldTable = this.hashTable;
        IDictionary<K,V>[] newTable = new IDictionary[primes[pindex]];
        this.hashTable = newTable;
        this.size = 0;

        for (int i = 0; i < oldTable.length; i++) {
            if (oldTable[i] != null){
                for (K key : oldTable[i]) {
                    this.put(key, oldTable[i].get(key));
                }
            }
        }
    }


    @Override
    public V put(K key, V value) {
        if (!containsKey(key)){
            if ((this.size+1)/this.hashTable.length >= 1){
                this.expand();
            }
            this.size++;
            if (this.hashTable[Math.abs(key.hashCode() % hashTable.length)] == null){
                this.hashTable[Math.abs(key.hashCode() % hashTable.length)] = this.chain.get();
                this.hashTable[Math.abs(key.hashCode() % hashTable.length)].put(key, value);
            }
            else{
                this.hashTable[Math.abs(key.hashCode() % hashTable.length)].put(key, value);
            }
            return null;
        }
        else {
            V old = this.hashTable[Math.abs(key.hashCode() % hashTable.length)].get(key);
            this.hashTable[Math.abs(key.hashCode() % hashTable.length)].put(key, value);
            return old;
        }

    }

    @Override
    public boolean containsKey(K key) {
        int bucketIndex = Math.abs(key.hashCode() % hashTable.length);
        if (hashTable[bucketIndex] == null){
            return false;
        }
        else {
            return hashTable[bucketIndex].containsKey(key);
        }
    }

    @Override
    public boolean containsValue(V value) {
        for (int i = 0; i < hashTable.length; i++){
            if (hashTable[i] != null && hashTable[i].containsValue(value)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        ICollection<K> keySet = new LinkedDeque();
        for (int i = 0; i < hashTable.length; i++){
            if (hashTable[i] != null){
                ((LinkedDeque<K>) keySet).addAll(hashTable[i].keys());
            }
        }
        return keySet;
    }

    @Override
    public ICollection<V> values() {
        ICollection<V> values = new LinkedDeque();
        for (int i = 0; i < hashTable.length; i++){
            if (hashTable[i] != null){
                ((LinkedDeque<V>) values).addAll(hashTable[i].values());
            }
        }
        return values;
    }

    @Override
    public Iterator<K> iterator() {
        return new CHIterator();
    }

//    @Override
//    public Iterator<K> iterator() {
//        return this.keySet().iterator();
//    }

}

