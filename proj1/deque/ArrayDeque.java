package deque;

/**Use mod to simplify the if-else */
public class ArrayDeque<AnyThing> {
    private AnyThing[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    /** Creates an empty list. */
    public ArrayDeque() {
        items = (AnyThing []) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    private void resize(int capacity) {
        AnyThing[] a = (AnyThing []) new Object[capacity];
        System.arraycopy(items, 0, a, 0, size);
        items = a;
    }
    public boolean isEmpty() {
        if ( size == 0) {
            return true;
        }
        return false;
    }

    /** Inserts X into the back of the list. */
    public void addLast(AnyThing x) {
        if (size == items.length) {
            resize (size * 2);
            nextLast += 8;
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
    public void addFirst(AnyThing x) {
        if (size == items.length) {
            resize (size * 2);
            nextFirst += 8;
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
    public AnyThing get(int index) {
        int realIndex = nextFirst + 1 + index;
        if (nextFirst > items.length - 1) {
            realIndex -= items.length;  // Can use mod to simplify.
        }
        return items[realIndex];
    }

    /** Returns the number of items in the list. */
    public int size() {
        return size;
    }

    /** Deletes item from back of the list and
     * returns deleted item. */
    public AnyThing removeLast() {
        if (size > 0) {
            AnyThing lstItem;
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
    public AnyThing removeFirst() {
        if (size > 0) {
            size -= 1;
            AnyThing fstItem;
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
    public void printDeque() {
        int keepTrack = size;
        int startPrint;
        if (nextFirst == items.length - 1) {
            startPrint = 0;
        } else {
            startPrint = nextFirst + 1;
        }
        while(keepTrack > 0) {
            keepTrack -= 1;
            System.out.print(items[startPrint] + " ");
            if (startPrint == items.length - 1) {
                startPrint = 0;
            } else {
                startPrint = nextFirst + 1;
            }
            startPrint += 1;
        }
        System.out.println();
    }
}
