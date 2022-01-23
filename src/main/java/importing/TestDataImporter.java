package importing;

import importing.api.IImporter;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;
import org.gradoop.temporal.model.impl.pojo.TemporalEdgeFactory;
import org.gradoop.temporal.model.impl.pojo.TemporalVertex;
import org.gradoop.temporal.model.impl.pojo.TemporalVertexFactory;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Importer that creates a predefined test graph.
 */
public class TestDataImporter implements IImporter {
    private ArrayList<TemporalVertex> vertices;
    private ArrayList<TemporalEdge> edges;

    /**
     * Constructor for TestDataImporter
     */
    public TestDataImporter() {
        createGraph();
    }

    @Override
    public ArrayList<TemporalEdge> getEdges() {
        return edges;
    }

    @Override
    public ArrayList<TemporalVertex> getVertices() {
        return vertices;
    }

    /**
     * Gets a vertex from the vertex list, if it exists, creates and adds it, if it doesn't.
     * @param factory
     * @param id Id of the vertex
     * @param label Label of the vertex
     * @return Id of the vertex as GradoopId
     */
    private GradoopId addOrGetVertex(TemporalVertexFactory factory, long id, String label) {
        TemporalVertex vertex = vertices.stream().filter(e -> e.getPropertyValue("id").getLong() == id).findFirst().orElse(null);
        if (vertex == null) {
            vertex = factory.createVertex(label);
            vertex.setProperty("id", id);
            vertices.add(vertex);
        }

        return vertex.getId();
    }

    /**
     * Adds an edge to the edge list.
     * @param factory
     * @param label Label of the edge
     * @param sourceVertexId Id of the origin vertex
     * @param targetVertexId Id of the destination vertex
     * @param validFrom Time, from which the edge exists
     * @param validTo Time, until which the edge exists
     */
    private void addEdge(TemporalEdgeFactory factory, String label, GradoopId sourceVertexId, GradoopId targetVertexId, long validFrom, long validTo) {
        TemporalEdge edge = factory.createEdge(label, sourceVertexId, targetVertexId);
        edge.setValidFrom(validFrom);
        edge.setValidTo(validTo);

        this.edges.add(edge);
    }

    /**
     * Generates the test graph.
     */
    private void createGraph() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        TemporalVertexFactory vertexFactory = new TemporalVertexFactory();
        TemporalEdgeFactory edgeFactory = new TemporalEdgeFactory();

        // 1+
        addEdge(vertexFactory, edgeFactory, "Edge 1", "E", "F", 7, 12);
        addEdge(vertexFactory, edgeFactory, "Edge 2", "E", "A", 13, 41);
        addEdge(vertexFactory, edgeFactory, "Edge 3", "C", "A", 26, 39);
        addEdge(vertexFactory, edgeFactory, "Edge 4", "H", "B", 3, 33);
        addEdge(vertexFactory, edgeFactory, "Edge 5", "J", "H", 5, 15);
        addEdge(vertexFactory, edgeFactory, "Edge 6", "G", "D", 32, 38);
        addEdge(vertexFactory, edgeFactory, "Edge 7", "E", "E", 3, 34);
        addEdge(vertexFactory, edgeFactory, "Edge 8", "F", "J", 16, 31);
        addEdge(vertexFactory, edgeFactory, "Edge 9", "H", "I", 13, 27);
        addEdge(vertexFactory, edgeFactory, "Edge 10", "D", "I", 22, 30);
        // 11+
        addEdge(vertexFactory, edgeFactory, "Edge 11", "A", "H", 14, 27);
        addEdge(vertexFactory, edgeFactory, "Edge 12", "D", "I", 20, 40);
        addEdge(vertexFactory, edgeFactory, "Edge 13", "B", "H", 17, 44);
        addEdge(vertexFactory, edgeFactory, "Edge 14", "I", "A", 32, 38);
        addEdge(vertexFactory, edgeFactory, "Edge 15", "H", "H", 3, 23);
        addEdge(vertexFactory, edgeFactory, "Edge 16", "B", "I", 12, 21);
        addEdge(vertexFactory, edgeFactory, "Edge 17", "J", "F", 27, 39);
        addEdge(vertexFactory, edgeFactory, "Edge 18", "D", "G", 22, 34);
        addEdge(vertexFactory, edgeFactory, "Edge 19", "A", "A", 8, 32);
        addEdge(vertexFactory, edgeFactory, "Edge 20", "C", "D", 28, 36);
        // 21+
        addEdge(vertexFactory, edgeFactory, "Edge 21", "F", "I", 19, 32);
        addEdge(vertexFactory, edgeFactory, "Edge 22", "J", "A", 5, 43);
        addEdge(vertexFactory, edgeFactory, "Edge 23", "E", "H", 3, 6);
        addEdge(vertexFactory, edgeFactory, "Edge 24", "F", "H", 13, 36);
        addEdge(vertexFactory, edgeFactory, "Edge 25", "D", "G", 10, 36);
        addEdge(vertexFactory, edgeFactory, "Edge 26", "C", "H", 10, 34);
        addEdge(vertexFactory, edgeFactory, "Edge 27", "H", "H", 11, 24);
        addEdge(vertexFactory, edgeFactory, "Edge 28", "G", "H", 39, 41);
        addEdge(vertexFactory, edgeFactory, "Edge 29", "C", "I", 37, 38);
        addEdge(vertexFactory, edgeFactory, "Edge 30", "G", "G", 4, 47);
        // 31+
        addEdge(vertexFactory, edgeFactory, "Edge 31", "H", "J", 33, 47);
        addEdge(vertexFactory, edgeFactory, "Edge 32", "I", "J", 17, 28);
        addEdge(vertexFactory, edgeFactory, "Edge 33", "I", "A", 22, 31);
        addEdge(vertexFactory, edgeFactory, "Edge 34", "C", "G", 10, 41);
        addEdge(vertexFactory, edgeFactory, "Edge 35", "A", "G", 2, 17);
        addEdge(vertexFactory, edgeFactory, "Edge 36", "F", "G", 16, 30);
        addEdge(vertexFactory, edgeFactory, "Edge 37", "C", "D", 22, 43);
        addEdge(vertexFactory, edgeFactory, "Edge 38", "H", "A", 11, 18);
        addEdge(vertexFactory, edgeFactory, "Edge 39", "H", "B", 7, 40);
        addEdge(vertexFactory, edgeFactory, "Edge 40", "C", "E", 26, 46);
        // 41+
        addEdge(vertexFactory, edgeFactory, "Edge 41", "A", "J", 39, 46);
        addEdge(vertexFactory, edgeFactory, "Edge 42", "C", "H", 27, 44);
        addEdge(vertexFactory, edgeFactory, "Edge 43", "B", "E", 31, 43);
        addEdge(vertexFactory, edgeFactory, "Edge 44", "H", "J", 6, 20);
        addEdge(vertexFactory, edgeFactory, "Edge 45", "E", "G", 13, 16);
        addEdge(vertexFactory, edgeFactory, "Edge 46", "I", "I", 15, 22);
        addEdge(vertexFactory, edgeFactory, "Edge 47", "J", "F", 4, 28);
        addEdge(vertexFactory, edgeFactory, "Edge 48", "D", "G", 9, 25);
        addEdge(vertexFactory, edgeFactory, "Edge 49", "D", "F", 12, 18);
        addEdge(vertexFactory, edgeFactory, "Edge 50", "G", "B", 8, 46);
    }

    /**
     * Adds an edge to the edge list.
     * @param vFactory VertexFactory
     * @param eFactory EdgeFactory
     * @param eLabel Label of the edge
     * @param fromId Id of the origin vertex
     * @param toId Id of the destination vertex
     * @param validFrom Time, from which the edge exists
     * @param validTo Time, until which the edge exists
     */
    private void addEdge(TemporalVertexFactory vFactory, TemporalEdgeFactory eFactory, String eLabel, int fromId, int toId, long validFrom, long validTo) {
        String[] vLabels = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
        this.addEdge(
                eFactory,
                eLabel,
                addOrGetVertex(vFactory, fromId, vLabels[(fromId - 1) % vLabels.length]),
                addOrGetVertex(vFactory, toId, vLabels[(toId - 1) % vLabels.length]),
                validFrom,
                validTo
        );
    }

    /**
     * Adds an edge to the edge list.
     * @param vFactory VertexFactory
     * @param eFactory EdgeFactory
     * @param eLabel Label of the edge
     * @param fromLabel Label of the origin vertex
     * @param toLabel Label of the destination vertex
     * @param validFrom Time, from which the edge exists
     * @param validTo Time, until which the edge exists
     */
    private void addEdge(TemporalVertexFactory vFactory, TemporalEdgeFactory eFactory, String eLabel, String fromLabel, String toLabel, long validFrom, long validTo) {
        TreeMap<String, Long> labelToId = new TreeMap<>();
        labelToId.put("A", new Long(1));
        labelToId.put("B", new Long(2));
        labelToId.put("C", new Long(3));
        labelToId.put("D", new Long(4));
        labelToId.put("E", new Long(5));
        labelToId.put("F", new Long(6));
        labelToId.put("G", new Long(7));
        labelToId.put("H", new Long(8));
        labelToId.put("I", new Long(9));
        labelToId.put("J", new Long(10));

        this.addEdge(
                eFactory,
                eLabel,
                addOrGetVertex(vFactory, labelToId.get(fromLabel.toUpperCase()), fromLabel),
                addOrGetVertex(vFactory, labelToId.get(toLabel.toUpperCase()), toLabel),
                validFrom,
                validTo
        );
    }
}
