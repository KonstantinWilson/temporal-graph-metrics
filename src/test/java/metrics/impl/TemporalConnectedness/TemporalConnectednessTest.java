package metrics.impl.TemporalConnectedness;

import importing.TestDataImporter;
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
    private TemporalConnectedness temporalConnectedness;

    @Before
    public void setUp() {
        TestDataImporter importer = new TestDataImporter();
        edges = importer.getEdges();
        vertices = importer.getVertices();
        temporalConnectedness = new TemporalConnectedness(getVertex("E").getId(), getVertex("J").getId());
    }

    @Test
    public void testEtoJ() {
        temporalConnectedness.calculate(edges);
        TreeMap<Long, Short> data = temporalConnectedness.getData().getData();
        System.out.println(data);

        assertEquals("Size is " + data.size() + " but should be " + 2 + ".", 2, data.size());

        assertEquals("Y at X=3 should be 1.", new Short((short)1), data.get(3L));
        assertEquals("Y at X=47 should be 0.", new Short((short)0), data.get(47L));
    }

    private TemporalVertex getVertex(String label) {
        return vertices.stream().filter(v -> v.getLabel().equals(label)).findFirst().orElse(null);
    }
}