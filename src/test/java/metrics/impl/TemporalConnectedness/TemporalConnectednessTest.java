package metrics.impl.TemporalConnectedness;

import importing.TestDataImporter;
import metrics.impl.HopCount.HopCount;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;
import org.gradoop.temporal.model.impl.pojo.TemporalVertex;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class TemporalConnectednessTest {
    private ArrayList<TemporalEdge> edges;
    private ArrayList<TemporalVertex> vertices;

    @Before
    public void setUp() {
        TestDataImporter importer = new TestDataImporter();
        edges = importer.getEdges();
        vertices = importer.getVertices();
    }

    @Test
    public void testBtoA() {
        TemporalConnectedness temporalConnectedness = new TemporalConnectedness(getVertex("B").getId(), getVertex("A").getId());
        temporalConnectedness.calculate(edges);
        TreeMap<Long, Short> data = temporalConnectedness.getData().getData();
        System.out.println(data);

        assertEquals("Size is " + data.size() + " but should be " + 2 + ".", 2, data.size());

        assertEquals("Y at X=12 should be 1.", new Short((short)1), data.get(12L));
        assertEquals("Y at X=38 should be 0.", new Short((short)0), data.get(38L));
    }

    @Test
    public void testEtoJ() {
        TemporalConnectedness temporalConnectedness = new TemporalConnectedness(getVertex("E").getId(), getVertex("J").getId());
        temporalConnectedness.calculate(edges);
        TreeMap<Long, Short> data = temporalConnectedness.getData().getData();
        System.out.println(data);

        assertEquals("Size is " + data.size() + " but should be " + 2 + ".", 2, data.size());

        assertEquals("Y at X=3 should be 1.", new Short((short)1), data.get(3L));
        assertEquals("Y at X=47 should be 0.", new Short((short)0), data.get(47L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSimilar() {
        new TemporalConnectedness(getVertex("G").getId(), getVertex("G").getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidNull() {
        new TemporalConnectedness(getVertex("E").getId(), null);
    }

    private TemporalVertex getVertex(String label) {
        return vertices.stream().filter(v -> v.getLabel().equals(label)).findFirst().orElse(null);
    }
}