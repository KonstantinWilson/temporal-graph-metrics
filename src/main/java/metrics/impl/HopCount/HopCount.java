package metrics.impl.HopCount;

import basics.StackItem;
import basics.diagram.DiagramV2;
import metrics.api.IMetric;
import org.apache.flink.api.java.tuple.Tuple2;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;

import java.util.*;
import java.util.stream.Collectors;

public class HopCount implements IMetric<Integer> {
    private GradoopId startId;
    private GradoopId endId;
    private ArrayList<TemporalEdge> oldEdges = new ArrayList<>();
    private DiagramV2<Long, Integer> diagram = new DiagramV2<>(null);

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
        this.diagram = new DiagramV2<>(null);
        getHopCounts(edges);
    }

    @Override
    public DiagramV2<Long, Integer> getData() {
        return this.diagram;
    }

    private void determineHopCounts(TemporalEdge edge) {
        List<TemporalEdge> edgesInTime = getEdgesBetween(edge.getValidFrom(), edge.getValidTo());
        edgesInTime.add(edge);
        getHopCounts(edgesInTime);
    }

    private List<TemporalEdge> getEdgesBetween(Long start, Long end) {
        return this.oldEdges.stream().filter(e -> e.getValidFrom() >= start && e.getValidTo() <= end).collect(Collectors.toList());
    }

    private int getHopCounts(List<TemporalEdge> edges) {
        int count = 0;

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
            System.out.print(lastIndex + "/" + firstSize  + " - " + stack.size() + "                                                                                \r");

            if (action == RecursiveAction.WENT_DEEPER || action == RecursiveAction.WENT_NEXT) {
                if (path.peek().getTargetId().equals(endId)) {
                    // Found result
                    //System.out.println("GEFUNDEN!");
                    //System.out.println(path.stream().map(v -> v.getLabel()).collect(Collectors.toList()));
                    Tuple2<Long, Long> trimmed = trim(path);
                    //System.out.println(trimmed);
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
                    /*System.out.println("nextSteps.size() = " + nextSteps.size());
                    System.out.println("lastId = " + lastEdge.getTargetId());*/
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

        return count;
    }

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
