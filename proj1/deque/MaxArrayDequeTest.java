package deque;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;
public class MaxArrayDequeTest {
    public static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }
    public static class IntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {  //Need to be Integer, not int.
            return o1 - o2;
        }
    }

    @Test
    public void testString() {
        StringComparator nc = new StringComparator(); // Do we need constructor?
        MaxArrayDeque<String> maxStringArray = new MaxArrayDeque(nc);
        maxStringArray.addLast("hand");
        maxStringArray.addLast("hair");
        maxStringArray.addLast("love");
        maxStringArray.addLast("pig");
        String res = maxStringArray.max();
        assertEquals("pig", res);
    }

    @Test
    public void testInt() {
        IntComparator ic = new IntComparator();
        MaxArrayDeque<Integer> maxIntArray = new MaxArrayDeque(ic);
        maxIntArray.addLast(188);
        maxIntArray.addLast(9999);
        maxIntArray.addLast(3);
        maxIntArray.addLast(7);
        int res = maxIntArray.max();
        assertEquals(9999, res);
    }

    @Test
    public void testIntMaxParam() {
        IntComparator ic = new IntComparator();
        StringComparator nc = new StringComparator();
        MaxArrayDeque<Integer> maxIntArray = new MaxArrayDeque(nc);
        maxIntArray.addLast(188);
        maxIntArray.addLast(9999);
        maxIntArray.addLast(3);
        maxIntArray.addLast(7);
        int res = maxIntArray.max(ic);
        assertEquals(9999, res);
    }
}
