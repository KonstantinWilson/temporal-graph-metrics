package metrics.impl.HopCount;

import importing.TestDataImporter;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;
import org.gradoop.temporal.model.impl.pojo.TemporalVertex;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class HopCountTest {
    private ArrayList<TemporalEdge> edges;
    private ArrayList<TemporalVertex> vertices;

    @Before
    public void setUp() {
        TestDataImporter importer = new TestDataImporter();
        edges = importer.getEdges();
        vertices = importer.getVertices();
    }

    @Test
    public void testDtoH() {
        HopCount hopCount = new HopCount(getVertex("D").getId(), getVertex("H").getId());
        hopCount.calculate(edges);
        TreeMap<Long, Integer> data = hopCount.getData().getData();

        int expectedSize = 3;
        assertEquals("Size is " + data.size() + " but should be " + expectedSize + ".", expectedSize, data.size());

        assertEquals("Y at X=13 should be 2.", new Integer(2), data.get(13L));
        assertEquals("Y at X=18 should be 3.", new Integer(3), data.get(18L));
        assertTrue("Y at X=36 should exist and be null.", data.containsKey(36L) && data.get(36L) == null);
    }

    @Test
    public void testAtoB() {
        HopCount hopCount = new HopCount(getVertex("A").getId(), getVertex("B").getId());
        hopCount.calculate(edges);
        TreeMap<Long, Integer> data = hopCount.getData().getData();

        int expectedSize = 2;
        assertEquals("Size is " + data.size() + " but should be " + expectedSize + ".", expectedSize, data.size());

        assertEquals("Y at X=8 should be 2.", new Integer(2), data.get(8L));
        assertTrue("Y at X=27 should exist and be null.", data.containsKey(27L) && data.get(27L) == null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSimilar() {
        HopCount hopCount = new HopCount(getVertex("A").getId(), getVertex("A").getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidNull() {
        HopCount hopCount = new HopCount(null, getVertex("G").getId());
    }

    private TemporalVertex getVertex(String label) {
        return vertices.stream().filter(v -> v.getLabel().equals(label)).findFirst().orElse(null);
    }
}