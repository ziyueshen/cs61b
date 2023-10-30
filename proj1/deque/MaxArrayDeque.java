package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{ // No need to implement Comparator, cuz we're not building a comparator
    /**Creates a MaxArrayDeque with the parameter c, which
     * is a Comparator that defines the rule of comparison(int value, string character etc.) */
    private Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c) {  //pass in a default Comparator at instantiation
        comparator = c;
    }

    /** No parameter, returns the maximum element in the deque
     * governed by the parameter c.*/
    public T max() {
        int maxDex = 0;
        for (int i = 0; i < size(); i++) {
            int res = comparator.compare(get(maxDex), get(i));
            if (res < 0) {
                maxDex = i;
            }
        }
        return get(maxDex);
    }

    /** Parameter is a Comparator, returns the maximum element in the deque
     * governed by the parameter.*/
    public T max(Comparator<T> c) { // pass in a new Comparator
        int maxDex = 0;
        for (int i = 0; i < size(); i++) {
            int res = c.compare(get(maxDex), get(i));
            if (res < 0) {
                maxDex = i;
            }
        }
        return get(maxDex);
    }
}
