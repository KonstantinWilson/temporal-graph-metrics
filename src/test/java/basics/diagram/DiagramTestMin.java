package basics.diagram;

import org.junit.Test;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class DiagramTestMin {
    @Test
    public void testSingleInsert() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(3L, 10L, 1);

        TreeMap<Long, Integer> data = diagram.getData();

        if (data.size() != 2) {
            fail("Size doesn't match");
        }
        assertEquals("", new Integer(1), data.get(3L));
        assertNull("", data.get(10L));
    }

    /**
     *      [---3---)
     * [===2===)
     */
    @Test
    public void testInsertBetterBefore() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(5L, 10L, 3);
        diagram.insertMin(3L, 7L, 2);

        TreeMap<Long, Integer> data = diagram.getData();

        if (data.size() != 3) {
            fail("Size should be 3 but is " + data.size());
        }
        assertEquals("Y at X=3 should be 2.", new Integer(2), data.get(3L));
        assertEquals("Y at X=7 should be 3.", new Integer(3), data.get(7L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     * [===3===)
     *      [---2---)
     */
    @Test
    public void testInsertWorseBefore() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(5L, 10L, 2);
        diagram.insertMin(3L, 7L, 3);

        TreeMap<Long, Integer> data = diagram.getData();

        if (data.size() != 3) {
            fail("Size should be 3 but is " + data.size());
        }
        assertEquals("Y at X=3 should be 3.", new Integer(3), data.get(3L));
        assertEquals("Y at X=5 should be 2.", new Integer(2), data.get(5L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     * [---3---)
     *      [===2===)
     */
    @Test
    public void testInsertBetterAfter() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(3L, 7L, 3);
        diagram.insertMin(5L, 10L, 2);

        TreeMap<Long, Integer> data = diagram.getData();

        if (data.size() != 3) {
            fail("Size should be 3 but is " + data.size());
        }
        assertEquals("Y at X=3 should be 3.", new Integer(3), data.get(3L));
        assertEquals("Y at X=5 should be 2.", new Integer(2), data.get(5L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     *      [===3===)
     * [---2---)
     */
    @Test
    public void testInsertWorseAfter() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(3L, 7L, 2);
        diagram.insertMin(5L, 10L, 3);

        TreeMap<Long, Integer> data = diagram.getData();

        if (data.size() != 3) {
            fail("Size should be 3 but is " + data.size());
        }
        assertEquals("Y at X=3 should be 2.", new Integer(2), data.get(3L));
        assertEquals("Y at X=7 should be 3.", new Integer(3), data.get(7L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     * [---3-----------)
     *      [===2===)
     */
    @Test
    public void testInsertBetterInside() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(3L, 10L, 3);
        diagram.insertMin(5L, 8L, 2);

        TreeMap<Long, Integer> data = diagram.getData();

        if (data.size() != 4) {
            fail("Size should be 4 but is " + data.size());
        }
        assertEquals("Y at X=3 should be 3.", new Integer(3), data.get(3L));
        assertEquals("Y at X=5 should be 2.", new Integer(2), data.get(5L));
        assertEquals("Y at X=8 should be 3.", new Integer(3), data.get(8L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     *      [===3===)
     * [---2-----------)
     */
    @Test
    public void testInsertWorseInside() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(3L, 10L, 2);
        diagram.insertMin(5L, 8L, 3);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 2;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=3 should be 2.", new Integer(2), data.get(3L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     * [---[===2===)---)
     */
    @Test
    public void testInsertSameInside() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(3L, 10L, 2);
        diagram.insertMin(5L, 8L, 2);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 2;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=3 should be 2.", new Integer(2), data.get(3L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     *      [---3---)
     * [===2============)
     */
    @Test
    public void testInsertBetterAround() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(5L, 8L, 3);
        diagram.insertMin(3L, 10L, 2);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 2;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=3 should be 2.", new Integer(2), data.get(3L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     * [===3============)
     *      [---2---)
     */
    @Test
    public void testInsertWorseAround() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(5L, 8L, 2);
        diagram.insertMin(3L, 10L, 3);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 4;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=3 should be 3.", new Integer(3), data.get(3L));
        assertEquals("Y at X=5 should be 2.", new Integer(2), data.get(5L));
        assertEquals("Y at X=8 should be 3.", new Integer(3), data.get(8L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     * [===[---2---)===)
     */
    @Test
    public void testInsertSameAround() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(5L, 8L, 2);
        diagram.insertMin(3L, 10L, 2);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 2;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=3 should be 2.", new Integer(2), data.get(3L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInsertInvalid() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(5L, 5L, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInsertInvalid2() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(10L, 5L, 2);
    }

    @Test
    public void testInsertTwoSimilar() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(2L, 5L, 4);
        diagram.insertMin(2L, 5L, 4);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 2;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=2 should be 4.", new Integer(4), data.get(2L));
        assertNull("Y at X=5 should be null.", data.get(5L));
    }

    @Test
    public void testInsertTwoSimilarButDifferentValue() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(2L, 5L, 4);
        diagram.insertMin(2L, 5L, 3);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 2;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=2 should be 3.", new Integer(3), data.get(2L));
        assertNull("Y at X=5 should be null.", data.get(5L));
    }

    @Test
    public void testInsertTwoSimilarOverlappingAfter() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(3L, 8L, 4);
        diagram.insertMin(5L, 10L, 4);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 2;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=3 should be 4.", new Integer(4), data.get(3L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    @Test
    public void testInsertTwoSimilarOverlappingBefore() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(5L, 10L, 4);
        diagram.insertMin(3L, 8L, 4);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 2;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=3 should be 4.", new Integer(4), data.get(3L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    @Test
    public void testMultipleInserts() {
        Diagram<Long, Integer> diagram = new Diagram<>(null);
        diagram.insertMin(4L, 13L, 5);
        diagram.insertMin(12L, 25L, 3);
        diagram.insertMin(9L, 18L, 4);
        diagram.insertMin(8L, 19L, 2);
        diagram.insertMin(12L, 27L, 5);
        diagram.insertMin(30L, 42L, 4);
        diagram.insertMin(21L, 35L, 3);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 5;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=4 should be 5.", new Integer(5), data.get(4L));
        assertEquals("Y at X=8 should be 2.", new Integer(2), data.get(8L));
        assertEquals("Y at X=19 should be 3.", new Integer(3), data.get(19L));
        assertEquals("Y at X=35 should be 4.", new Integer(4), data.get(35L));
        assertNull("Y at X=42 should be null.", data.get(42L));
    }
}