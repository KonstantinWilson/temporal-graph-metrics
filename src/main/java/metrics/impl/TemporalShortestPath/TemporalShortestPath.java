package metrics.impl.TemporalShortestPath;

import basics.ComparableObject;
import basics.StackItem;
import basics.diagram.Diagram;
import export.CSVExporter;
import export.ImageExporter;
import importing.TestDataImporter;
import metrics.api.IMetric;
import metrics.impl.HopCount.RecursiveAction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.common.model.impl.pojo.EPGMElement;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the Temporal Shortest Path from "Graph Metrics for Temporal Networks" by Vincenzo Nicosia et. al.
 * The Temporal Shortest Path is the path with overlapping edges from on vertex to another with the shortest temporal length. The amount of hops is irellevant.
 */
public class TemporalShortestPath implements IMetric<ComparableObject<Long, List<TemporalEdge>>> {
    private final GradoopId startId;
    private final GradoopId endId;
    private final ArrayList<TemporalEdge> oldEdges = new ArrayList<>();
    private Diagram<Long, ComparableObject<Long, List<TemporalEdge>>> diagram = new Diagram<>(null);

    /**
     * Constructor of TemporalShortestPath
     * @param startId Id of the origin vertex
     * @param endId Id of the destination vertex
     */
    public TemporalShortestPath(GradoopId startId, GradoopId endId) {
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
        this.oldEdges.clear();
        this.oldEdges.addAll(edges);
        this.diagram = new Diagram<>(null);
        determine(edges);
    }

    @Override
    public Diagram<Long, ComparableObject<Long, List<TemporalEdge>>> getData() {
        return diagram;
    }

    /**
     * Selects edges that overlap with 'edge'.
     * @param edge Edge to compare other edges to.
     * @return List of TemporalEdges
     */
    private List<TemporalEdge> selectRelevantEdges(TemporalEdge edge) {
        List<TemporalEdge> filtered = oldEdges.stream().filter(o ->
                o.getValidFrom() < edge.getValidTo() && o.getValidTo() > edge.getValidFrom()).collect(Collectors.toList());
        filtered.add(edge);
        return filtered;
    }

    /**
     * Determines the TemporalShortestPath from a given list of edges. Stores the results in a diagram.
     * @param edges List of TemporalEdges
     */
    private void determine(List<TemporalEdge> edges) {
        Stack<StackItem<TemporalEdge>> stack = new Stack<>();
        Stack<TemporalEdge> path = new Stack<>();
        Stack<GradoopId> edgePath = new Stack<>();
        RecursiveAction action = RecursiveAction.WENT_DEEPER;
        stack.push(new StackItem<>(edges.stream().filter(e -> e.getSourceId().equals(startId) && !e.getTargetId().equals(startId)).collect(Collectors.toList()), Long.MIN_VALUE, Long.MAX_VALUE));
        path.push(stack.peek().next());
        edgePath.push(path.peek().getSourceId());
        edgePath.push(path.peek().getTargetId());

        while (stack.size() > 0) {
            if (action == RecursiveAction.WENT_DEEPER || action == RecursiveAction.WENT_NEXT) {
                if (path.peek().getTargetId().equals(endId)) {
                    Tuple2<Long, Long> trimmed = trim(path);
                    if (trimmed.f0 < trimmed.f1) {
                        diagram.insertMin(
                                trimmed.f0,
                                trimmed.f1,
                                new ComparableObject<>(trimmed.f1 - trimmed.f0, new ArrayList<>(path))
                        );
                    }

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
    }

    /**
     * Determines the timeframe in which a stack of edges occurs.
     * @param stack Stack of edges
     * @return Tuple with two Longs. The first number is the start time and the second number is the end time.
     */
    private Tuple2<Long, Long> trim(Stack<TemporalEdge> stack) {
        long start = 0, end = 0;
        boolean init = true;
        for (TemporalEdge next : stack) {
            if (init) {
                start = next.getValidFrom();
                end = next.getValidTo();
                init = false;
            } else {
                start = Math.max(start, next.getValidFrom());
                end = Math.min(end, next.getValidTo());
            }
        }

        return new Tuple2<>(start, end);
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

        TemporalShortestPath metric = new TemporalShortestPath(
                importer.getVertices().stream().filter(v -> v.getLabel().equals(originInput)).findFirst().get().getId(),
                importer.getVertices().stream().filter(v -> v.getLabel().equals(destinationInput)).findFirst().get().getId()
        );
        metric.calculate(importer.getEdges());

        for (Map.Entry<Long, ComparableObject<Long, List<TemporalEdge>>> e: metric.getData().getData().entrySet()) {
            StringBuilder sb = new StringBuilder()
                    .append(e.getKey())
                    .append("=");
            if (e.getValue() == null) {
                sb.append("null");
            }
            else {
                sb.append(e.getValue().getNumber())
                        .append(":")
                        .append(e.getValue().getObject().stream().map(TemporalEdge::getLabel).collect(Collectors.toList()));
            }
            System.out.println(sb);
        }

        CSVExporter exporter = new CSVExporter("TemporalShortestPath.csv");
        ImageExporter imgExporter = new ImageExporter(512,512, 16);
        try {
            ArrayList<Long> keys = new ArrayList(metric.getData().getData().keySet());
            List<String> values = metric.getData().getData().entrySet().stream().map(e -> {
                StringBuilder sb = new StringBuilder();
                if (e.getValue() == null) {
                    sb.append("null");
                }
                else {
                    sb.append(e.getValue().getNumber())
                            .append(" ")
                            .append(e.getValue().getObject().stream().map(TemporalEdge::getLabel).collect(Collectors.toList()));
                }
                return sb.toString();
            }).collect(Collectors.toList());
            exporter.save(keys, values);
            imgExporter.draw(metric.getData());
            if (!imgExporter.save("TemporalShortestPath.png", true)) {
                System.out.println("Error saving png-file.");
            }
        }
        catch (Exception e) {
            System.out.println("Error saving csv-file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
