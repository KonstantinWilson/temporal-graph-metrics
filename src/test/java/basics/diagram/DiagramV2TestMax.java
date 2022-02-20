package basics.diagram;

import org.junit.Test;

import java.util.TreeMap;

import static org.junit.Assert.*;

public class DiagramV2TestMax {
    @Test
    public void testSingleInsert() {
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(3L, 10L, 1);

        TreeMap<Long, Integer> data = diagram.getData();

        if (data.size() != 2) {
            fail("Size doesn't match");
        }
        assertEquals("", new Integer(1), data.get(3L));
        assertNull("", data.get(10L));
    }

    /**
     * [===3===)
     *      [---2---)
     */
    @Test
    public void testInsertBetterBefore() {
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(5L, 10L, 2);
        diagram.insertMax(3L, 7L, 3);

        TreeMap<Long, Integer> data = diagram.getData();

        if (data.size() != 3) {
            fail("Size should be 3 but is " + data.size());
        }
        assertEquals("Y at X=3 should be 3.", new Integer(3), data.get(3L));
        assertEquals("Y at X=7 should be 2.", new Integer(2), data.get(7L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     *      [---3---)
     * [===2===)
     */
    @Test
    public void testInsertWorseBefore() {
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(5L, 10L, 3);
        diagram.insertMax(3L, 7L, 2);

        TreeMap<Long, Integer> data = diagram.getData();

        if (data.size() != 3) {
            fail("Size should be 3 but is " + data.size());
        }
        assertEquals("Y at X=3 should be 2.", new Integer(2), data.get(3L));
        assertEquals("Y at X=5 should be 3.", new Integer(3), data.get(5L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     *      [===3===)
     * [---2---)
     */
    @Test
    public void testInsertBetterAfter() {
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(3L, 7L, 2);
        diagram.insertMax(5L, 10L, 3);

        TreeMap<Long, Integer> data = diagram.getData();

        if (data.size() != 3) {
            fail("Size should be 3 but is " + data.size());
        }
        assertEquals("Y at X=3 should be 2.", new Integer(2), data.get(3L));
        assertEquals("Y at X=5 should be 3.", new Integer(3), data.get(5L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     * [---3---)
     *      [===2===)
     */
    @Test
    public void testInsertWorseAfter() {
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(3L, 7L, 3);
        diagram.insertMax(5L, 10L, 2);

        TreeMap<Long, Integer> data = diagram.getData();

        if (data.size() != 3) {
            fail("Size should be 3 but is " + data.size());
        }
        assertEquals("Y at X=3 should be 3.", new Integer(3), data.get(3L));
        assertEquals("Y at X=7 should be 2.", new Integer(2), data.get(7L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     *      [===3===)
     * [---2-----------)
     */
    @Test
    public void testInsertBetterInside() {
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(3L, 10L, 2);
        diagram.insertMax(5L, 8L, 3);

        TreeMap<Long, Integer> data = diagram.getData();

        if (data.size() != 4) {
            fail("Size should be 4 but is " + data.size());
        }
        assertEquals("Y at X=3 should be 2.", new Integer(2), data.get(3L));
        assertEquals("Y at X=5 should be 3.", new Integer(3), data.get(5L));
        assertEquals("Y at X=8 should be 2.", new Integer(2), data.get(8L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     * [---3-----------)
     *      [===2===)
     */
    @Test
    public void testInsertWorseInside() {
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(3L, 10L, 3);
        diagram.insertMax(5L, 8L, 2);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 2;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=3 should be 3.", new Integer(3), data.get(3L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     * [---[===2===)---)
     */
    @Test
    public void testInsertSameInside() {
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(3L, 10L, 2);
        diagram.insertMax(5L, 8L, 2);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 2;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=3 should be 3.", new Integer(2), data.get(3L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     * [===3============)
     *      [---2---)
     */
    @Test
    public void testInsertBetterAround() {
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(5L, 8L, 2);
        diagram.insertMax(3L, 10L, 3);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 2;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=3 should be 3.", new Integer(3), data.get(3L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     *      [---3---)
     * [===2============)
     */
    @Test
    public void testInsertWorseAround() {
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(5L, 8L, 3);
        diagram.insertMax(3L, 10L, 2);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 4;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=3 should be 2.", new Integer(2), data.get(3L));
        assertEquals("Y at X=5 should be 3.", new Integer(3), data.get(5L));
        assertEquals("Y at X=8 should be 2.", new Integer(2), data.get(8L));
        assertNull("Y at X=10 should be null.", data.get(10L));
    }

    /**
     * [===[---2---)===)
     */
    @Test
    public void testInsertSameAround() {
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(5L, 8L, 2);
        diagram.insertMax(3L, 10L, 2);

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
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(5L, 5L, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInsertInvalid2() {
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(10L, 5L, 2);
    }

    @Test
    public void testInsertTwoSimilar() {
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(2L, 5L, 4);
        diagram.insertMax(2L, 5L, 4);

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
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(2L, 5L, 3);
        diagram.insertMax(2L, 5L, 4);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 2;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=2 should be 4.", new Integer(4), data.get(2L));
        assertNull("Y at X=5 should be null.", data.get(5L));
    }

    @Test
    public void testInsertTwoSimilarOverlappingAfter() {
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(3L, 8L, 4);
        diagram.insertMax(5L, 10L, 4);

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
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(5L, 10L, 4);
        diagram.insertMax(3L, 8L, 4);

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
        DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);
        diagram.insertMax(12L, 25L, 3);
        diagram.insertMax(9L, 18L, 4);
        diagram.insertMax(8L, 19L, 2);
        diagram.insertMax(12L, 27L, 5);
        diagram.insertMax(30L, 42L, 4);
        diagram.insertMax(21L, 35L, 3);
        diagram.insertMax(4L, 13L, 5);

        TreeMap<Long, Integer> data = diagram.getData();

        int expectedSize = 4;
        if (data.size() != expectedSize) {
            fail("Size should be " + expectedSize + " but is " + data.size());
        }
        assertEquals("Y at X=4 should be 5.", new Integer(5), data.get(4L));
        assertEquals("Y at X=27 should be 3.", new Integer(3), data.get(27L));
        assertEquals("Y at X=30 should be 4.", new Integer(4), data.get(30L));
        assertNull("Y at X=42 should be null.", data.get(42L));
    }
}