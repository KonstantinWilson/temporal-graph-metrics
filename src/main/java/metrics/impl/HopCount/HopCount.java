package metrics.impl.HopCount;

import basics.StackItem;
import basics.diagram.Diagram;
import export.CSVExporter;
import export.ImageExporter;
import importing.TestDataImporter;
import metrics.api.IMetric;
import org.apache.flink.api.java.tuple.Tuple2;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.common.model.impl.pojo.EPGMElement;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the temporalized Hop Count from "Classification of graph metrics" by Javier Martín Hernández and Piet Van Mieghem.
 * The Hop Count is the lowest amount of hops to travel from a vertex j to another vertex k.
 */
public class HopCount implements IMetric<Integer> {
    private final GradoopId startId;
    private final GradoopId endId;
    private final ArrayList<TemporalEdge> oldEdges = new ArrayList<>();
    private Diagram<Long, Integer> diagram = new Diagram<>(null);

    /**
     * Constructor of Hop Count
     * @param startId Id of the origin vertex.
     * @param endId Id of the destination vertex.
     */
    public HopCount(GradoopId startId, GradoopId endId) {
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
        determineHopCounts(edge);
        this.oldEdges.add(edge);
    }

    @Override
    public void calculate(List<TemporalEdge> edges) {
        this.diagram = new Diagram<>(null);
        this.oldEdges.clear();
        this.oldEdges.addAll(edges);
        determine(edges);
    }

    @Override
    public Diagram<Long, Integer> getData() {
        return this.diagram;
    }

    /**
     * Selects edges in timespan of 'edge' and determines the Hop Count with the new edge.
     * @param edge New edge to add
     */
    private void determineHopCounts(TemporalEdge edge) {
        List<TemporalEdge> edgesInTime = getEdgesBetween(edge.getValidFrom(), edge.getValidTo());
        edgesInTime.add(edge);
        determine(edgesInTime);
    }

    /**
     * Returns all edges that are valid between 'start' and 'end' time. Overlaps are included.
     * @param start Valid from time
     * @param end Valid to time
     * @return List of edges
     */
    private List<TemporalEdge> getEdgesBetween(Long start, Long end) {
        return this.oldEdges.stream().filter(e -> e.getValidFrom() < end && e.getValidTo() > start).collect(Collectors.toList());
    }

    /**
     * Determines the Hop Counts between the vertices 'startId' and 'endId'. Stores the result in a Diagram.
     * @param edges List of edges to work with
     */
    private void determine(List<TemporalEdge> edges) {
        Stack<StackItem<TemporalEdge>> stack = new Stack<>();
        Stack<TemporalEdge> path = new Stack<>();
        Stack<GradoopId> nodePath = new Stack<>();
        RecursiveAction action = RecursiveAction.WENT_DEEPER;
        stack.push(new StackItem<>(edges.stream().filter(e -> e.getSourceId().equals(startId)).collect(Collectors.toList()), Long.MIN_VALUE, Long.MAX_VALUE));
        path.push(stack.peek().next());
        nodePath.push(path.peek().getSourceId());
        nodePath.push(path.peek().getTargetId());

        while (stack.size() > 0) {
            if (action == RecursiveAction.WENT_DEEPER || action == RecursiveAction.WENT_NEXT) {
                if (path.peek().getTargetId().equals(endId)) {
                    Tuple2<Long, Long> trimmed = trim(path);
                    if (trimmed.f0 < trimmed.f1) {
                        diagram.insertMin(trimmed.f0, trimmed.f1, path.size());
                    }

                    path.pop();
                    nodePath.pop();
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
                            && !nodePath.contains(e.getTargetId())
                    ).collect(Collectors.toList());

                    if (nextSteps.size() <= 0) {
                        path.pop();
                        nodePath.pop();
                        TemporalEdge next = stack.peek().next();
                        if (next == null) {
                            stack.pop();
                            action = RecursiveAction.WENT_BACK;
                        }
                        else {
                            path.push(next);
                            nodePath.push(path.peek().getTargetId());
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
                        nodePath.push(path.peek().getTargetId());
                        action = RecursiveAction.WENT_DEEPER;
                    }
                }
            }
            else if (action == RecursiveAction.WENT_BACK) {
                path.pop();
                nodePath.pop();
                TemporalEdge next = stack.peek().next();
                if (next == null) {
                    stack.pop();
                }
                else {
                    path.push(next);
                    nodePath.push(path.peek().getTargetId());
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

        HopCount metric = new HopCount(
                importer.getVertices().stream().filter(v -> v.getLabel().equals(originInput)).findFirst().get().getId(),
                importer.getVertices().stream().filter(v -> v.getLabel().equals(destinationInput)).findFirst().get().getId()
        );
        metric.calculate(importer.getEdges());
        System.out.println(metric.getData().getData());
        CSVExporter exporter = new CSVExporter("HopCount.csv");
        ImageExporter imgExporter = new ImageExporter(512,512, 16);
        try {
            exporter.save(metric.getData());
            imgExporter.draw(metric.getData());
            if (!imgExporter.save("HopCount.png", true)) {
                System.out.println("Error saving png-file.");
            }
        }
        catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
