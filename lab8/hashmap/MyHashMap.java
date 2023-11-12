package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Ziyue Shen
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    int DEFAULT_SIZE = 16;
    double DEFAULT_FACTOR = 0.75;
    int MULTI_FACTOR = 2;

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    // Members with private access modifier are only accessible
    // within the same class. They are not visible to other classes,
    // even subclasses. Protected members are inherited by subclasses.
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }
    private int size;
    private int initialSize;
    private double maxLoad;
    /* Instance Variables */
    // private Collection<Node> bucket;
    private Collection<Node>[] buckets;  // buckets is actually an array;
    // You should probably define some more!

    // how to create an array of collection with certain size?

    /** Constructors */
    public MyHashMap() {
        this.initialSize = DEFAULT_SIZE;
        this.maxLoad = DEFAULT_FACTOR;
        this.buckets = new Collection[this.initialSize];
        for (int i = 0; i < buckets.length; i++) { // replace with factor method
            this.buckets[i] = createBucket();      // fill buckets with empty bucket
        }
    }


    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        this.maxLoad = DEFAULT_FACTOR;
        this.buckets = new Collection[this.initialSize];
        for (int i = 0; i < buckets.length; i++) { // replace with factor method
            this.buckets[i] = createBucket();      // fill buckets with empty bucket
        }
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.maxLoad = maxLoad;
        this.buckets = new Collection[this.initialSize];
        for (int i = 0; i < buckets.length; i++) { // replace with factor method
            this.buckets[i] = createBucket();      // fill buckets with empty bucket
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return null;
    }  // Factory method

    /**
     * Returns a data structure(AL/HS/TS/PQ) to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * !!Override!! this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO !!CALL!! THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }   // Factory method

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return null;
    }    // Factory method

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        size = 0;
        buckets = null;  // buckets is the body of HashMap
    }

    /** Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {  // the key in user's perspective; not hashcode
        if (buckets == null) {
            return false;
        }
        int originCode = key.hashCode();
        int actualCode = Math.floorMod(originCode, initialSize);
        Collection<Node> bucket = buckets[actualCode];
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {  // implement containsKey first
        if (buckets == null) {
            return null;
        }
        int originCode = key.hashCode();
        int actualCode = Math.floorMod(originCode, initialSize);
        Collection<Node> bucket = buckets[actualCode];
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    /** Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    @Override
    public void put(K key, V value) { // resizing; replace
        if (buckets == null) {
            buckets = new Collection[this.initialSize];
            for (int i = 0; i < buckets.length; i++) { // replace with factor method
                this.buckets[i] = createBucket();      // fill buckets with empty bucket
            }
        }
        int originCode = key.hashCode();
        int actualCode = Math.floorMod(originCode, initialSize);
        // find the bucket with the key equals hashcode, using index
        // add() the node to the bucket
        // need to traverse the bucket to see if key already exists
        Collection<Node> bucket = buckets[actualCode];
        for (Node node: bucket) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        bucket.add(new Node(key, value));  // add() is implemented by the bucket data structure
        size += 1;
        double load = (double) size / initialSize;
        // casting in Java
        if (load > this.maxLoad) {
            resize();
        }
    }

    private void resize() {
        initialSize *= MULTI_FACTOR;
        Collection<Node>[] new_buckets = new Collection[initialSize];
        for (int i = 0; i < new_buckets.length; i++) {
            new_buckets[i] = createBucket();      // fill new_buckets with empty bucket
        }
        // how to traverse HashMap?
        for (int i = 0; i < initialSize / MULTI_FACTOR; i++) {
            Collection<Node> bucket = buckets[i];
            for (Node node: bucket) {
                int originCode = node.key.hashCode();
                int actualCode = Math.floorMod(originCode, initialSize);
                // read old buckets, add to new buckets; then point to new buckets
                new_buckets[actualCode].add(node);
            }
        }
        buckets = new_buckets;
    }

    /** Returns a Set view of the keys contained in this map. */
    @Override
    public Set<K> keySet() {
        if (buckets == null) {
            return null;
        }
        Set<K> mapKeySet = new HashSet<>();
        for (int i = 0; i < initialSize; i++) {
            Collection<Node> bucket = buckets[i];
            for (Node node : bucket) {
                mapKeySet.add(node.key);
            }
        }
        return mapKeySet;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }
    @Override
    public Iterator<K> iterator() {
        return new HashMapIter();
    }

    private class HashMapIter implements Iterator<K> {

        /**
         * Create a new HashMapIter by setting cur to the first node in the
         * array that stores the bucket. Bucket is also a iterator.
         */
        private Iterator<K> keys;
        HashMapIter() {
            // i = 0;
            keys = keySet().iterator();
            //  = buckets[i];
        }

        @Override
        public boolean hasNext() {
            return keys.hasNext();
        }

        @Override
        public K next() {
            if (keys.hasNext()) {
//                curIter = cur.iterator();
//                Node n = curIter.next();
//                // how to access? MUST create an iterator first to call next()
//                K key = n.key;
//                i += 1;
//                cur = buckets[i];
                return keys.next();
            }
            return null;
        }


        /** Stores the current key-value pair. */
        private Collection<Node> cur;  // cur is a bucket
        private Iterator<Node> curIter;  // MUST declare Iterator first.
        private int i;

    }

}
