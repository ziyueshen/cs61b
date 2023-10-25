package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE


    @Test
    public void testBuggyAList() {
        AListNoResizing<Integer> lstNoBug= new AListNoResizing<> ();
        BuggyAList<Integer> lstBuggy = new BuggyAList<> ();
        lstNoBug.addLast(1);
        lstBuggy.addLast(1);
        lstNoBug.addLast(2);
        lstBuggy.addLast(2);
        lstNoBug.addLast(3);
        lstBuggy.addLast(3);
        assertEquals(lstNoBug.size(), lstBuggy.size());
        assertEquals(lstNoBug.removeLast(), lstBuggy.removeLast()); // assertEquals compares the return value x
        assertEquals(lstNoBug.removeLast(), lstBuggy.removeLast());
        assertEquals(lstNoBug.removeLast(), lstBuggy.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<> ();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                // System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size2 = B.size();
                // System.out.println("size: " + size);
                assertEquals(size, size2);
            } else if (operationNumber == 2 && L.size() > 0) {
                int last = L.getLast();
                int last2 = B.getLast();
                assertEquals(last, last2);
            } else if (operationNumber == 3 && L.size() > 0) {
                int rmv = L.removeLast();
                int rmv2 = B.removeLast();
                assertEquals(rmv, rmv2);
            }
        }
    }
}
