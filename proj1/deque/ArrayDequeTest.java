package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void testAddGet() {
        ArrayDeque<Integer> testDeque= new ArrayDeque<> ();
        for (int i = 1; i <= 8; i ++) {
            testDeque.addLast(i);
            testDeque.printDeque();
        }
        for (int i = 0; i <= 7; i ++) {
            int j = testDeque.get(i);
            System.out.print(j);
        }
    }

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        LinkedListDeque<Integer> B = new LinkedListDeque<> ();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
                L.printDeque();
                B.printDeque();
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size2 = B.size();
                System.out.println("size: " + size + size2);
                assertEquals(size, size2);
            } else if (operationNumber == 2 && L.size() > 0) {
                int randVal = StdRandom.uniform(0, L.size());
                System.out.println("get: " + randVal);
                L.printDeque();
                // System.out.println(Arrays.toString(L.items));
                B.printDeque();
                int last = L.get(randVal);
                int last2 = B.get(randVal);
                assertEquals(last, last2);
            } else if (operationNumber == 3 && L.size() > 0) {
                int rmv = L.removeLast();
                int rmv2 = B.removeLast();
                System.out.println("rmv: " + (L.size() - 1));
                assertEquals(rmv, rmv2);
            }
        }
    }

//    public static void main(String[] args) {
//        testAddGet();
//    }
}
