package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{
    int size = 0;
    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        size = 0;
        node = null;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        if (node == null) {
            return false;
        }
        return node.get(key, node) != null;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        if (node == null) {
            return null;
        }
        BSTNode lookup = node.get(key, node);
        if (lookup == null) {
            return null;
        }
        return (V) lookup.val;
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K k, V val) {
        if (node != null) {
            if (k.equals(node.key)) {
                node.val = val;
            } else {
                node = node.insert(k, val, node);
                size = size + 1;
            }
        } else {
            node = new BSTNode(k, val, null, null);
            size = size + 1;
        }
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    private BSTNode node;

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    private class BSTNode<K extends Comparable<K>, V> {
        K key;
        V val;
        BSTNode left, right;
        /** Constructor*/
        BSTNode(K k, V v, BSTNode left, BSTNode right) {
            this.key = k;
            this.val = v;
            this.left = left;
            this.right = right;
        }

//        BSTNode left() {
//            return this.left;
//        }
//        BSTNode right() {
//            return this.right;
//        }
        BSTNode get(K k, BSTNode node) {
            if (k.equals(node.key)) {
                return node;
            }
            if (node == null) {
                return null;
            }
            if (k.compareTo((K) node.key) < 0) {
                return get(k, node.left);
            } else {
                return get(k, node.right);
            }
        }

        BSTNode insert(K k, V val, BSTNode node) {
            if (node == null) {
                return new BSTNode(k, val, null, null);
            }
            if (k.compareTo((K) node.key) < 0) {
                node.left = insert(k, val, node.left);
            } else {
                node.right = insert(k, val, node.right);
            }
            return node;
        }

    }
}
