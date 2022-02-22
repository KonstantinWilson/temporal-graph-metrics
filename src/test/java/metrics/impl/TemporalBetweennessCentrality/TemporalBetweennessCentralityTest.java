package metrics.impl.TemporalBetweennessCentrality;

import importing.TestDataImporter;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;
import org.gradoop.temporal.model.impl.pojo.TemporalVertex;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class TemporalBetweennessCentralityTest {
    final double THRESHOLD = 0.0001;
    private ArrayList<TemporalEdge> edges;
    private ArrayList<TemporalVertex> vertices;

    @Before
    public void setUp() {
        TestDataImporter importer = new TestDataImporter();
        edges = importer.getEdges();
        vertices = importer.getVertices();
    }

    @Test
    public void testA() {
        TemporalBetweennessCentrality temporalBetweennessCentrality = new TemporalBetweennessCentrality(vertices, getVertex("A").getId());
        temporalBetweennessCentrality.calculate(edges);
        TreeMap<Long, Double> data = temporalBetweennessCentrality.getData().getData();

        System.out.println(data);

        int expectedSize = 2;
        assertEquals("Size is " + data.size() + " but should be " + expectedSize + ".", expectedSize, data.size());

        TemporalEdge floorEdge = edges.stream().reduce((a, b) -> a.getValidFrom() < b.getValidFrom() ? a : b).orElse(null);
        TemporalEdge ceilEdge = edges.stream().reduce((a, b) -> a.getValidTo() > b.getValidFrom() ? a : b).orElse(null);
        assert floorEdge != null;
        assert ceilEdge != null;
        System.out.println("Floor: " + floorEdge.getValidFrom());

        double expected = 0.40689866083635423;
        double actual = data.get(floorEdge.getValidFrom());
        assertTrue("Y at X=" + floorEdge.getValidFrom() + " should be " + expected + " but is " + actual + ".", Math.abs(expected - actual) < THRESHOLD);
        assertNull("Y at X=36 should be null.", data.get(ceilEdge.getValidTo()));
    }

    @Test
    public void testE() {
        TemporalBetweennessCentrality temporalBetweennessCentrality = new TemporalBetweennessCentrality(vertices, getVertex("E").getId());
        temporalBetweennessCentrality.calculate(edges);
        TreeMap<Long, Double> data = temporalBetweennessCentrality.getData().getData();

        System.out.println(data);

        int expectedSize = 2;
        assertEquals("Size is " + data.size() + " but should be " + expectedSize + ".", expectedSize, data.size());

        TemporalEdge floorEdge = edges.stream().reduce((a, b) -> a.getValidFrom() < b.getValidFrom() ? a : b).orElse(null);
        TemporalEdge ceilEdge = edges.stream().reduce((a, b) -> a.getValidTo() > b.getValidFrom() ? a : b).orElse(null);
        assert floorEdge != null;
        assert ceilEdge != null;
        System.out.println("Floor: " + floorEdge.getValidFrom());

        double expected = 0.031236124986124984;
        double actual = data.get(floorEdge.getValidFrom());
        assertTrue("Y at X=" + floorEdge.getValidFrom() + " should be " + expected + " but is " + actual + ".", Math.abs(expected - actual) < THRESHOLD);
        assertNull("Y at X=36 should be null.", data.get(ceilEdge.getValidTo()));
    }

    private TemporalVertex getVertex(String label) {
        return vertices.stream().filter(v -> v.getLabel().equals(label)).findFirst().orElse(null);
    }
}