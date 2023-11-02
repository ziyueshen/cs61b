package deque;

import java.util.Iterator;

/**Use mod to simplify the if-else */
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;  // public for testing, change to private
    private int size;
    private int nextFirst;
    private int nextLast;
    /** Creates an empty list. */
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        // Must have to avoid compilation error.
        if (other.size() != size)    {
            // Cuz we use .size() here which belongs to LinkedListDeque.
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(other.get(i))) {
                // Cannot use ==. Cuz T is a general object.
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator<T> implements Iterator<T> {
        private int wizPos;
        ArrayDequeIterator() {
            wizPos = 0;
        }

        @Override
        public boolean hasNext() {
            return wizPos < size;
        }

        @Override
        public T next() {
            T returnedItem = (T) get(wizPos);
            wizPos += 1;
            return returnedItem;
        }
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];

        if (capacity > items.length) {
            System.arraycopy(items, 0, a, 0, nextLast);
            System.arraycopy(items, (nextFirst + 1) % items.length, a,
                    (nextFirst + 1 + capacity - items.length) % capacity, items.length - nextLast);
        } else {
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null) {
                    a[i % capacity] = items[i];
                }
            }
        }
        items = a;    // Add '% capacity' for debugging.
    }
    //    @Override
    //    public boolean isEmpty() {
    //        if ( size == 0) {
    //            return true;
    //        }
    //        return false;
    //    }

    /** Inserts X into the back of the list. */
    @Override
    public void addLast(T x) {
        if (size == items.length) {
            resize(size * 2);
            if (nextLast == 0) {
                nextLast += items.length / 2;
            }
            if (nextFirst != items.length) {
                nextFirst += items.length / 2;
            }
        }
        items[nextLast] = x;
        if (nextLast < items.length - 1) {
            nextLast += 1;
        } else {
            nextLast = 0;
        }
        size += 1;
    } 
    /** Inserts X into the front of the list. */
    @Override
    public void addFirst(T x) {
        if (size == items.length) {
            resize(size * 2);
            if (nextLast == 0) {
                nextLast += items.length / 2;
            }
            if (nextFirst != items.length) {
                nextFirst += items.length / 2;
            }
        }
        items[nextFirst] = x;
        if (nextFirst > 0) {
            nextFirst -= 1;
        } else {
            nextFirst = items.length - 1;
        }
        size += 1;
    }

    /** Gets the ith item in the list (0 is the front). */
    @Override
    public T get(int index) {
        if (index < items.length) {
            int realIndex = nextFirst + 1 + index;
            if (realIndex > items.length - 1) {
                realIndex -= items.length;  // Can use mod to simplify.
            }
            return items[realIndex];
        } else {
            return null;
        }
    }

    /** Returns the number of items in the list. */
    @Override
    public int size() {
        return size;
    }

    /** Deletes item from back of the list and
     * returns deleted item. */
    @Override
    public T removeLast() {
        if (size == items.length / 4) {
            resize(items.length / 2);
            nextFirst = nextFirst % items.length;
            nextLast = nextLast % items.length;
        }
        if (size > 0) {
            T lstItem;
            size -= 1;
            if (nextLast == 0) {
                lstItem = items[items.length - 1];
                items[items.length - 1] = null;
                nextLast = items.length - 1;
            } else {
                lstItem = items[nextLast - 1];
                items[nextLast - 1] = null;
                nextLast -= 1;
            }
            return lstItem;
        }
        return null;
    }
    /** Deletes item from front of the list and
     * returns deleted item. */
    @Override
    public T removeFirst() {
        if (size == items.length / 4) {
            resize(items.length / 2);
            nextFirst = nextFirst % items.length;
            nextLast = nextLast % items.length;
        }
        if (size > 0) {
            size -= 1;
            T fstItem;
            if (nextFirst == items.length - 1) {
                fstItem = items[0];
                items[0] = null;
                nextFirst = 0;
            } else {
                fstItem = items[nextFirst + 1];
                items[nextFirst + 1] = null;
                nextFirst += 1;
            }
            return fstItem;
        }
        return null;
    }

    /** Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.*/
    @Override
    public void printDeque() {
        int keepTrack = size;
        int startPrint;
        if (nextFirst == items.length - 1) {
            startPrint = 0;
        } else {
            startPrint = nextFirst + 1;
        }
        while (keepTrack > 0) {
            keepTrack -= 1;
            System.out.print(items[startPrint] + " ");
            if (startPrint == items.length - 1) {
                startPrint = 0;
            } else {
                startPrint = startPrint + 1;
            }
        }
        System.out.println();
    }
}
