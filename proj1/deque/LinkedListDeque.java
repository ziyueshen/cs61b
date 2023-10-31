package deque;
/** A double ended queue.Use double sentinel.*/
public class LinkedListDeque<AnyThing> implements Deque<AnyThing>{
    /** The structure underneath the Deque.*/
    private class AnyNode {
        private AnyThing item;
        private AnyNode next;
        private AnyNode prev;  // Circular structure.

        private AnyNode(AnyThing i, AnyNode n, AnyNode p) {  //Constructor, like__init__.
            prev = p;
            item = i;
            next = n;
        }
    }
    // AnyNode first;
    AnyNode sentinel;
    int size;
    /** Create an empty list*/
    public LinkedListDeque() {  // Two ways to instantiate, 0 element or 1 element.
        sentinel = new AnyNode(null, null, null);  // Use null as the initial value, cause later it could be reassigned to any type
        sentinel.next =sentinel;
        sentinel.prev =sentinel;
        size = 0;
    }
    public LinkedListDeque(AnyThing x) {  //To instantiate, pass in an item.
        // first = new AnyNode(x, null);  // Note: first is the body of LinkedListDeque object; first.item = x; first.next
        sentinel = new AnyNode(null, null, null);
        sentinel.next = new AnyNode(x, sentinel, sentinel);
        sentinel.prev = new AnyNode(x, sentinel, sentinel);
        size = 1;
    }
    /** Add an element to the front.*/
    @Override
    public void addFirst(AnyThing x) {
        // first = new AnyNode(x, first); //Important! Must create a new AnyNode.
        AnyNode newNode = new AnyNode(x, sentinel.next, sentinel);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;  // Never mutate sentinel itself.
        size += 1;
    }
    /** Add an element to the back.*/
    @Override
    public void addLast(AnyThing x) {
        size += 1;
        AnyNode newNode = new AnyNode(x, sentinel, sentinel.prev);
        sentinel.prev.next =  newNode;
        sentinel.prev = newNode;
        /*
        AnyNode p = first;
        // Move p until it reaches the end of the list.
        while (p.next != null) {
            p = p.next;
        }
        p.next = new AnyNode(x, null);
        */
    }
    /** Removes and returns the item at the front of the deque. If no such item exists, returns null.*/
    @Override
    public AnyThing removeFirst() {
        if (size > 0) {
            size -= 1;
        }
        AnyThing fstItem = sentinel.next.item;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        return fstItem;
    }
    /** Removes and returns the item at the back of the deque. If no such item exists, returns null.*/
    @Override
    public AnyThing removeLast() {
        if (size > 0) {
            size -= 1;
        }
        AnyThing lstItem = sentinel.prev.item;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        return lstItem;
    }
    /** Returns true if deque is empty, false otherwise.*/
//    @Override
//    public boolean isEmpty() {
//        if (sentinel.next.item == null) {
//            return true;
//        }
//        return false;
//    }
    /**  Returns the number of items in the deque.*/
    @Override
    public int size() {
        return size;
    }

    /** Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.*/
    @Override
    public void printDeque() {
        AnyNode p = sentinel.next;
        // Move p until it reaches the end of the list.
        while (p != sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }
    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * Must use iteration, not recursion.*/
    @Override
    public AnyThing get(int index) {
        AnyNode p = sentinel.next;
        int keepTrack = 0;
        while (keepTrack < index) {
            p = p.next;
            keepTrack += 1;
        }
        return p.item;
    }
    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * Must use recursion.*/
    public AnyThing getRecursive(int index) {
        AnyThing res;
        res = getHelper(sentinel.next, index);
        return res;
    }
    /** Helper for getRecursive.*/
    private AnyThing getHelper(AnyNode p, int index) {
        if (index == 0) {
            return p.item;
        } else {
            return getHelper(p.next, index - 1);
        }
    }
}
