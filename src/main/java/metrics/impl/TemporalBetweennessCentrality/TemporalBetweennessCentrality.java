package metrics.impl.TemporalBetweennessCentrality;

import basics.StackItem;
import basics.diagram.Diagram;
import export.CSVExporter;
import export.ImageExporter;
import importing.TestDataImporter;
import metrics.api.IMetric;
import basics.RecursiveAction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.common.model.impl.pojo.EPGMElement;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;
import org.gradoop.temporal.model.impl.pojo.TemporalElement;
import org.gradoop.temporal.model.impl.pojo.TemporalVertex;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Implementation of the Temporal Betweenness Centrality from "Graph Metrics for Temporal Networks" by Vincenzo Nicosia et. al.
 * The Temporal Betweenness Centrality says, how traversed a vertex is.
 * TBC = 1 / ((amountOfVertices-1) * (amountOfVertices-2)) * SUM( (amountOfShortestPaths(j,k) / amountOfShortestPathsThroughVertex(j,k)d) )
 */
public class TemporalBetweennessCentrality implements IMetric<Double> {
    private final GradoopId vertexId;
    private final List<TemporalVertex> vertices;
    private Diagram<Long, Double> result = null;
    private final long from;
    private final long to;

    /**
     * Constructor of TemporalBetweennessCentrality
     * @param vertices All vertices of the graph.
     * @param vertexId Id of vertex, for which the metric shall be determined.
     */
    public TemporalBetweennessCentrality(List<TemporalVertex> vertices, GradoopId vertexId) {
        this(vertices, vertexId, -1, -1);
    }

    /**
     * Constructor of TemporalBetweennessCentrality
     * @param vertices All vertices of the graph.
     * @param vertexId Id of vertex, for which the metric shall be determined.
     * @param from Start of the time span of which the metric shall be calculated. Set -1 to not specify a start.
     * @param to End of the time span of which the metric shall be calculated. Set -1 to not specify an end.
     */
    public TemporalBetweennessCentrality(List<TemporalVertex> vertices, GradoopId vertexId, long from, long to) {
        if (vertexId == null || vertices == null) {
            throw new IllegalArgumentException("Arguments can't be null.");
        }
        if (vertices.stream().noneMatch(v -> v.getId().equals(vertexId))) {
            throw new IllegalArgumentException("VertexId doesn't exist in the given list of vertices.");
        }
        this.vertices = vertices;
        this.vertexId = vertexId;
        this.from = from;
        this.to = to;
    }

    @Override
    public void calculate(TemporalEdge edge) {
        // TODO Implement edge-by-edge/streaming calculation.
    }

    // 1 / ((N-1)*(N-2)) * SUMME(Anzahl kürzester Pfade/Anzahl kürzester Pfade durch Knoten)
    @Override
    public void calculate(List<TemporalEdge> edges) {
        List<TemporalEdge> affectedEdges;
        if (from < 0 && to < 0) {
            affectedEdges = edges;
        }
        else {
            affectedEdges = edges.stream().filter(e -> (from < 0 || e.getValidFrom() >= from) && (to < 0 || e.getValidTo() <= to)).collect(Collectors.toList());
        }

        double f1 = 1 / ((double)(vertices.size() - 1) * (double)(vertices.size() - 2));
        double f2 = 0;

        for (TemporalVertex sourceVertex: vertices) {
            for (TemporalVertex targetVertex: vertices) {
                if (!sourceVertex.getId().equals(targetVertex.getId())
                        && !sourceVertex.getId().equals(vertexId)
                        && !targetVertex.getId().equals(vertexId)
                ) {
                    Tuple2<Long, Long> result = determine(affectedEdges, sourceVertex.getId(), targetVertex.getId());
                    if (result.f1 != 0) {
                        f2 += result.f0.doubleValue() / result.f1.doubleValue();
                    }
                }
            }
        }

        // Search the smallest ValidFrom time
        Long start = affectedEdges.stream().min(Comparator.comparing(TemporalElement::getValidFrom)).orElse(new TemporalEdge()).getValidFrom();
        // Search the biggest ValidTo time
        Long end = affectedEdges.stream().max(Comparator.comparing(TemporalElement::getValidTo)).orElse(new TemporalEdge()).getValidTo();

        result = new Diagram<>(null);
        if (start != null) {
            result.insertMin(start, end, f1 * f2);
        }
    }

    /**
     * Determines the Shortest Paths between two vertices.
     * @param edges Edges which shall be used to find the shortest paths.
     * @param startId Id of the origin vertex
     * @param endId Id of the destination vertex
     * @return Touple with two Long. The first number is the amount of shortest paths not traversing through vertexId. The second number is the amount of shortest paths traversing through vertexId.
     */
    private Tuple2<Long, Long> determine(List<TemporalEdge> edges, GradoopId startId, GradoopId endId) {
        Tuple2<Long, Long> fraction = new Tuple2<>(0L, 0L);

        Stack<StackItem<TemporalEdge>> stack = new Stack<>();
        Stack<TemporalEdge> path = new Stack<>();
        Stack<GradoopId> edgePath = new Stack<>();
        RecursiveAction action = RecursiveAction.WENT_DEEPER;
        stack.push(new StackItem<>(edges.stream().filter(e -> e.getSourceId().equals(startId)).collect(Collectors.toList()), Long.MIN_VALUE, Long.MAX_VALUE));

        if (stack.peek().current() == null) {
            return fraction;
        }

        path.push(stack.peek().next());

        edgePath.push(path.peek().getSourceId());
        edgePath.push(path.peek().getTargetId());

        while (stack.size() > 0) {
            // Falls in nächsten Rekursionsschritt
            if (action == RecursiveAction.WENT_DEEPER || action == RecursiveAction.WENT_NEXT) {
                if (path.peek().getTargetId().equals(endId)) {
                    // Found result
                    if (edgePath.contains(this.vertexId)) {
                        fraction.f0++;
                    }
                    fraction.f1++;

                    path.pop();
                    edgePath.pop();
                    stack.pop();
                    action = RecursiveAction.WENT_BACK;
                }
                else {
                    TemporalEdge lastEdge = path.peek();
                    long from = Math.max(stack.peek().getPreviousFrom(), lastEdge.getValidFrom());
                    long to = Math.min(stack.peek().getPreviousTo(), lastEdge.getValidTo());
                    List<TemporalEdge> nextSteps = edges.stream().filter(e ->
                            e.getSourceId().equals(lastEdge.getTargetId())
                                    && e.getValidFrom() < to
                                    && e.getValidTo() > from
                                    && !edgePath.contains(e.getTargetId())
                    ).collect(Collectors.toList());

                    if (nextSteps.size() <= 0) {
                        path.pop();
                        edgePath.pop();
                        TemporalEdge next = stack.peek().next();
                        if (next == null) {
                            stack.pop();
                            action = RecursiveAction.WENT_BACK;
                        }
                        else {
                            path.push(next);
                            edgePath.push(path.peek().getTargetId());
                            action = RecursiveAction.WENT_NEXT;
                        }
                    }
                    else {
                        stack.push(new StackItem<>(
                                nextSteps,
                                from, //Math.min(stack.peek().getPreviousFrom(), path.peek().getValidFrom()),
                                to //Math.max(stack.peek().getPreviousTo(), path.peek().getValidTo())
                        ));
                        path.push(stack.peek().next());
                        edgePath.push(path.peek().getTargetId());
                        action = RecursiveAction.WENT_DEEPER;
                    }
                }
            }
            else if (action == RecursiveAction.WENT_BACK) {
                path.pop();
                edgePath.pop();
                TemporalEdge next = stack.peek().next();
                if (next == null) {
                    stack.pop();
                }
                else {
                    path.push(next);
                    edgePath.push(path.peek().getTargetId());
                    action = RecursiveAction.WENT_NEXT;
                }
            }
        }
        return fraction;
    }

    @Override
    public Diagram<Long, Double> getData() {
        return result;
    }

    public static void main(String[] args) {
        TestDataImporter importer = new TestDataImporter();
        List<String> vertexLabels = importer.getVertices().stream().map(EPGMElement::getLabel).collect(Collectors.toList());
        System.out.print("Please choose a vertex " + vertexLabels + ": ");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        do {
            try {
                input = reader.readLine();
                if (input == null || !vertexLabels.contains(input)) {
                    System.out.print("Please choose a vertex from the list " + vertexLabels + ": ");
                    input = null;
                }
            }
            catch (Exception e) {
                System.out.println("Something went wrong: " + e.getMessage());
            }
        } while (input == null);
        String finalInput = input;

        TemporalBetweennessCentrality metric = new TemporalBetweennessCentrality(
                importer.getVertices(),
                importer.getVertices().stream().filter(v -> v.getLabel().equals(finalInput)).findFirst().get().getId(),
                10 ,
                -1
        );
        metric.calculate(importer.getEdges());
        System.out.println(metric.getData().getData());
        CSVExporter exporter = new CSVExporter("TemporalBetweennessCentrality.csv");
        ImageExporter imgExporter = new ImageExporter(512,512, 16);
        try {
            exporter.save(metric.getData());
            imgExporter.draw(metric.getData());
            if (!imgExporter.save("TemporalBetweennesCentrality.png", true)) {
                System.out.println("Error saving png-file.");
            }
        }
        catch (Exception e) {
            System.out.println("Error saving csv: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
