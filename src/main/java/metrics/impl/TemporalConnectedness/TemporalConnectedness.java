package metrics.impl.TemporalConnectedness;

import basics.StackItem;
import basics.diagram.Diagram;
import export.CSVExporter;
import export.ImageExporter;
import importing.TestDataImporter;
import metrics.api.IMetric;
import metrics.impl.HopCount.RecursiveAction;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.common.model.impl.pojo.EPGMElement;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Implementation of the Temporal Connectedness from "Graph Metrics for Temporal Networks" by Vincenzo Nicosia et. al.
 * Two vertices j and k are Temporally Connected, if there is a path from j to k with no overlapping edges and one edge occurs before the next one.
 */
public class TemporalConnectedness implements IMetric<Short> {
    private final GradoopId startId;
    private final GradoopId endId;
    private final ArrayList<TemporalEdge> oldEdges = new ArrayList<>();
    private Diagram<Long, Short> diagram = new Diagram<>((short)0);

    /**
     * Constructor of TemporalConnectedness
     * @param startId Id of the origin vertex
     * @param endId Id of the destination vertex
     */
    public TemporalConnectedness(GradoopId startId, GradoopId endId) {
        if (startId == null || endId == null) {
            throw new IllegalArgumentException("Id can't be null.");
        }
        if (startId.equals(endId)) {
            throw new IllegalArgumentException("StartId and EndId can't be the same.");
        }
        this.startId = startId;
        this.endId = endId;
    }

    @Override
    public void calculate(TemporalEdge edge) {
        determine(selectRelevantEdges(edge));
        oldEdges.add(edge);
    }

    @Override
    public void calculate(List<TemporalEdge> edges) {
        this.diagram = new Diagram((short)0);
        this.oldEdges.clear();
        this.oldEdges.addAll(edges);
        determine(edges);
    }

    @Override
    public Diagram<Long, Short> getData() {
        return this.diagram;
    }

    /**
     * Selects edges that don't overlap with 'edge'.
     * @param edge Edge to compare other edges to.
     * @return List of TemporalEdges
     */
    private List<TemporalEdge> selectRelevantEdges(TemporalEdge edge) {
        List<TemporalEdge> filtered = oldEdges.stream().filter(o -> o.getValidFrom() > edge.getValidTo() || o.getValidTo() < edge.getValidFrom()).collect(Collectors.toList());
        filtered.add(edge);
        return filtered;
    }

    /**
     * Determines the TemporalConnectedness from a given list of edges. Stores the results in a diagram.
     * @param edges List of TemporalEdges
     */
    private void determine(List<TemporalEdge> edges) {
        Stack<StackItem<TemporalEdge>> stack = new Stack<>();
        Stack<TemporalEdge> path = new Stack<>();
        Stack<GradoopId> vertexPath = new Stack<>();
        RecursiveAction action = RecursiveAction.WENT_DEEPER;
        stack.push(new StackItem<>(
                edges.stream().filter(e -> e.getSourceId().equals(this.startId)).collect(Collectors.toList()),
                Long.MIN_VALUE,
                Long.MAX_VALUE
        ));
        path.push(stack.peek().next());
        vertexPath.push(path.peek().getSourceId());
        vertexPath.push(path.peek().getTargetId());

        while (stack.size() > 0) {
            if (action == RecursiveAction.WENT_DEEPER || action == RecursiveAction.WENT_NEXT) {
                if (path.peek().getTargetId().equals(endId)) {
                    diagram.insertMax(path.get(0).getValidFrom(), path.peek().getValidTo(), (short)1);

                    path.pop();
                    vertexPath.pop();
                    stack.pop();
                    action = RecursiveAction.WENT_BACK;
                }
                else {
                    TemporalEdge lastEdge = path.peek();
                    List<TemporalEdge> nextEdges = edges.stream().filter(e ->
                            e.getSourceId().equals(lastEdge.getTargetId())
                                    && e.getValidFrom() >= lastEdge.getValidTo()
                                    && !vertexPath.contains(e.getTargetId())
                    ).collect(Collectors.toList());

                    if (nextEdges.size() <= 0) {
                        path.pop();
                        vertexPath.pop();
                        TemporalEdge next = stack.peek().next();
                        if (next == null) {
                            stack.pop();
                            action = RecursiveAction.WENT_BACK;
                        }
                        else {
                            path.push(next);
                            vertexPath.push(path.peek().getTargetId());
                            action = RecursiveAction.WENT_NEXT;
                        }
                    }
                    else {
                        stack.push(new StackItem<>(
                                nextEdges,
                                0,
                                0
                        ));
                        path.push(stack.peek().next());
                        vertexPath.push(path.peek().getTargetId());
                        action = RecursiveAction.WENT_DEEPER;
                    }
                }
            }
            else if (action == RecursiveAction.WENT_BACK) {
                path.pop();
                vertexPath.pop();
                TemporalEdge next = stack.peek().next();
                if (next == null) {
                    stack.pop();
                }
                else {
                    path.push(next);
                    vertexPath.push(path.peek().getTargetId());
                    action = RecursiveAction.WENT_NEXT;
                }
            }
        }
    }

    public static void main(String[] args) {
        TestDataImporter importer = new TestDataImporter();
        List<String> vertexLabels = importer.getVertices().stream().map(EPGMElement::getLabel).sorted().collect(Collectors.toList());

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        do {
            System.out.print("Please choose the origin vertex from the list " + vertexLabels + ": ");
            try {
                input = reader.readLine();
                if (input == null || !vertexLabels.contains(input)) {
                    input = null;
                }
            }
            catch (Exception e) {
                System.out.println("Something went wrong: " + e.getMessage());
            }
        } while (input == null);
        String originInput = input;

        input = null;
        do {
            System.out.print("Please choose the destination vertex from the list " + vertexLabels + ": ");
            try {
                input = reader.readLine();
                if (input == null || !vertexLabels.contains(input)) {
                    input = null;
                }
            }
            catch (Exception e) {
                System.out.println("Something went wrong: " + e.getMessage());
            }
        } while (input == null);
        String destinationInput = input;

        TemporalConnectedness metric = new TemporalConnectedness(
                importer.getVertices().stream().filter(v -> v.getLabel().equals(originInput)).findFirst().get().getId(),
                importer.getVertices().stream().filter(v -> v.getLabel().equals(destinationInput)).findFirst().get().getId()
        );
        metric.calculate(importer.getEdges());
        System.out.println(metric.getData().getData());
        CSVExporter exporter = new CSVExporter("TemporalConnectedness.csv");
        ImageExporter imgExporter = new ImageExporter(512,512, 16);
        try {
            exporter.save(metric.getData());
            imgExporter.draw(metric.getData());
            if (!imgExporter.save("TemporalConnectedness.png", true)) {
                System.out.println("Error saving png-file.");
            }
        }
        catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
