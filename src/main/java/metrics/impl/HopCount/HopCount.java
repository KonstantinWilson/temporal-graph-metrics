package metrics.impl.HopCount;

import basics.StackItem;
import basics.diagram.Diagram;
import metrics.api.IMetric;
import org.apache.flink.api.java.tuple.Tuple2;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the temporalized Hop Count from "Classification of graph metrics" by Javier Martín Hernández and Piet Van Mieghem.
 * The Hop Count is the lowest amount of hops to travel from a vertex j to another vertex k.
 */
public class HopCount implements IMetric<Integer> {
    private GradoopId startId;
    private GradoopId endId;
    private ArrayList<TemporalEdge> oldEdges = new ArrayList<>();
    private Diagram<Long, Integer> diagram = new Diagram<>(null);

    /**
     * Constructor of Hop Count
     * @param startId Id of the origin vertex.
     * @param endId Id of the destination vertex.
     */
    public HopCount(GradoopId startId, GradoopId endId) {
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
        return this.oldEdges.stream().filter(e -> e.getValidFrom() >= start && e.getValidTo() <= end).collect(Collectors.toList());
    }

    /**
     * Determines the Hop Counts between the vertices 'startId' and 'endId'. Stores the result in a Diagram.
     * @param edges List of edges to work with
     */
    private void determine(List<TemporalEdge> edges) {
        Stack<StackItem<TemporalEdge>> stack = new Stack<>();
        Stack<TemporalEdge> path = new Stack<>();
        Stack<GradoopId> edgePath = new Stack<>();
        RecursiveAction action = RecursiveAction.WENT_DEEPER;
        stack.push(new StackItem<TemporalEdge>(edges.stream().filter(e -> e.getSourceId().equals(startId)).collect(Collectors.toList()), Long.MIN_VALUE, Long.MAX_VALUE));
        path.push(stack.peek().next());
        edgePath.push(path.peek().getSourceId());
        edgePath.push(path.peek().getTargetId());

        int lastIndex = -1;
        int firstSize = stack.peek().size();
        while (stack.size() > 0) {
            if (stack.size() == 1) {
                lastIndex = stack.peek().getIndex();
            }

            if (action == RecursiveAction.WENT_DEEPER || action == RecursiveAction.WENT_NEXT) {
                if (path.peek().getTargetId().equals(endId)) {
                    Tuple2<Long, Long> trimmed = trim(path);
                    if (trimmed.f0 < trimmed.f1) {
                        diagram.insertMin(trimmed.f0, trimmed.f1, path.size());
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

                    if (nextSteps == null || nextSteps.size() <= 0) {
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
                    action = RecursiveAction.WENT_BACK;
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
        Iterator<TemporalEdge> it = stack.iterator();
        while (it.hasNext()) {
            TemporalEdge next = it.next();
            if (init) {
                start = next.getValidFrom();
                end = next.getValidTo();
                init = false;
            }
            else {
                start = Math.max(start, next.getValidFrom());
                end = Math.min(end, next.getValidTo());
            }
        }

        return new Tuple2<>(start, end);
    }
}
