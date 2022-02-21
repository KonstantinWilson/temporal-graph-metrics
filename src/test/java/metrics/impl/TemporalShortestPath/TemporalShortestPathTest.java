package metrics.impl.TemporalShortestPath;

import basics.ComparableObject;
import importing.TestDataImporter;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;
import org.gradoop.temporal.model.impl.pojo.TemporalVertex;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TemporalShortestPathTest {
    private ArrayList<TemporalEdge> edges;
    private ArrayList<TemporalVertex> vertices;

    @Before
    public void setUp() {
        TestDataImporter importer = new TestDataImporter();
        edges = importer.getEdges();
        vertices = importer.getVertices();
    }

    @Test
    public void testEtoJ() {
        TemporalShortestPath temporalShortestPath = new TemporalShortestPath(getVertex("E").getId(), getVertex("J").getId());
        temporalShortestPath.calculate(edges);
        TreeMap<Long, ComparableObject<Long, List<TemporalEdge>>> data = temporalShortestPath.getData().getData();

        int expectedSize = 6;
        assertEquals("Size is " + data.size() + " but should be " + expectedSize + ".", expectedSize, data.size());

        assertEquals("Y at X=14 should be 6.", new Long(6), data.get(14L).getNumber());
        assertEquals("Y at X=17 should be 4.", new Long(4), data.get(17L).getNumber());
        assertEquals("Y at X=21 should be 10.", new Long(10), data.get(21L).getNumber());
        assertNull("Y at X=27 should be null.", data.get(27L));
        assertEquals("Y at X=39 should be 2.", new Long(2), data.get(39L).getNumber());
        assertNull("Y at X=41 should be null.", data.get(41L));
    }

    @Test
    public void testDtoH() {
        TemporalShortestPath temporalShortestPath = new TemporalShortestPath(getVertex("D").getId(), getVertex("H").getId());
        temporalShortestPath.calculate(edges);
        TreeMap<Long, ComparableObject<Long, List<TemporalEdge>>> data = temporalShortestPath.getData().getData();

        int expectedSize = 9;
        assertEquals("Size is " + data.size() + " but should be " + expectedSize + ".", expectedSize, data.size());

        assertEquals("Y at X=13 should be 5.", new Long(5), data.get(13L).getNumber());
        assertEquals("Y at X=16 should be 2.", new Long(2), data.get(16L).getNumber());
        assertEquals("Y at X=18 should be 8.", new Long(8), data.get(18L).getNumber());
        assertEquals("Y at X=20 should be 7.", new Long(7), data.get(20L).getNumber());
        assertEquals("Y at X=22 should be 5.", new Long(5), data.get(22L).getNumber());
        assertEquals("Y at X=27 should be 1.", new Long(1), data.get(27L).getNumber());
        assertEquals("Y at X=28 should be 12.", new Long(12), data.get(28L).getNumber());
        assertEquals("Y at X=34 should be 19.", new Long(19), data.get(34L).getNumber());
        assertNull("Y at X=36 should be null.", data.get(41L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid() {
        new TemporalShortestPath(getVertex("E").getId(), getVertex("E").getId());
    }

    private TemporalVertex getVertex(String label) {
        return vertices.stream().filter(v -> v.getLabel().equals(label)).findFirst().orElse(null);
    }
}