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
    private HopCount hopCount;

    @Before
    public void setUp() {
        TestDataImporter importer = new TestDataImporter();
        edges = importer.getEdges();
        vertices = importer.getVertices();
        hopCount = new HopCount(getVertex("D").getId(), getVertex("H").getId());
    }

    @Test
    public void testDtoH() {
        hopCount.calculate(edges);
        TreeMap<Long, Integer> data = hopCount.getData().getData();

        assertEquals("Size is " + data.size() + " but should be " + 3 + ".", 3, data.size());

        assertEquals("Y at X=13 should be 2.", new Integer(2), data.get(13L));
        assertEquals("Y at X=18 should be 3.", new Integer(3), data.get(18L));
        assertNull("Y at X=36 should be null.", data.get(36L));
    }

    private TemporalVertex getVertex(String label) {
        return vertices.stream().filter(v -> v.getLabel().equals(label)).findFirst().orElse(null);
    }
}