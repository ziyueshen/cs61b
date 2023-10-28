package deque;

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
    public boolean isEmpty() {
        if ( size == 0) {
            return true;
        }
        return false;
    }

    /** Inserts X into the back of the list. */
    public void addLast(AnyThing x) {
        if (nextLast < items.length) {
            items[nextLast] = x;
            nextLast += 1;
        } else {
            items[0] = x;
            nextLast = 1;
        }
        size += 1;
    }
    /** Inserts X into the front of the list. */
    public void addFirst(AnyThing x) {
        if (nextFirst >= 0) {
            items[nextFirst] = x;
            nextFirst -= 1;
        } else {
            items[items.length - 1] = x;
            nextFirst = items.length - 2;
        }
        size += 1;
    }

    /** Gets the ith item in the list (0 is the front). */
    public AnyThing get(int index) {
        int realIndex;
        if (nextFirst == items.length - 1) {
            realIndex = index;
        } else {
            realIndex = nextFirst + 1 + index;
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
            System.out.print(items[startPrint]);
            if (startPrint == items.length - 1) {
                startPrint = 0;
            } else {
                startPrint = nextFirst + 1;
            }
            startPrint += 1;
        }
    }
}
