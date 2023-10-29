package flik;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestFlik {
    @Test
    public void testEqualFlik() {
        int a = 10;
        int b = 10;
        boolean res = Flik.isSameNumber(a, b);
        assertTrue("a should be equal to b", res);
    }
    @Test
    public void testUnequalFlik() {
        int a = 10;
        int b = 9;
        boolean res = Flik.isSameNumber(a, b);
        assertFalse("a should not be equal to b", res);
    }
    @Test
    public void testEqualFlikSteve() {
        int a = 128;
        int b = 128;
        boolean res = Flik.isSameNumber(a, b);
        assertTrue("a should be equal to b", res);
    }
    @Test
    public void testEqualFlikSteve2() {
        int a = 128;
        int b = a;
        boolean res = Flik.isSameNumber(a, b);
        assertTrue("a should be equal to b", res);
    }
}
