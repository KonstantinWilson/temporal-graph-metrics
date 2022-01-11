package metrics.impl.TemporalConnectedness;

import basics.StackItem;
import basics.diagram.DiagramV2;
import metrics.api.IMetric;
import metrics.impl.HopCount.RecursiveAction;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class TemporalConnectedness implements IMetric<Short> {
    private GradoopId startId;
    private GradoopId endId;
    private ArrayList<TemporalEdge> oldEdges = new ArrayList<>();
    private DiagramV2<Long, Short> diagram = new DiagramV2<Long, Short>((short)0);

    public TemporalConnectedness(GradoopId startId, GradoopId endId) {
        this.startId = startId;
        this.endId = endId;
    }

    @Override
    public void calculate(TemporalEdge edge) {

    }

    @Override
    public void calculate(List<TemporalEdge> edges) {
        determine(edges);
    }

    @Override
    public DiagramV2<Long, Short> getData() {
        return this.diagram;
    }

    private List<TemporalEdge> selectRelevantEdges(TemporalEdge edge) {
        List<TemporalEdge> filtered = oldEdges.stream().filter(o -> o.getValidFrom() > edge.getValidTo() || o.getValidTo() < edge.getValidFrom()).collect(Collectors.toList());
        filtered.add(edge);
        return filtered;
    }

    private void determine(List<TemporalEdge> edges) {
        Stack<StackItem<TemporalEdge>> stack = new Stack();
        Stack<TemporalEdge> path = new Stack();
        Stack<GradoopId> vertexPath = new Stack();
        RecursiveAction action = RecursiveAction.WENT_DEEPER;
        stack.push(new StackItem<>(
                edges.stream().filter(e -> e.getSourceId().equals(this.startId)).collect(Collectors.toList()),
                Long.MIN_VALUE,
                Long.MAX_VALUE
        ));
        path.push(stack.peek().next());
        vertexPath.push(path.peek().getSourceId());
        vertexPath.push(path.peek().getTargetId());

        int lastIndex = -1;
        int firstSize = stack.peek().size();

        while (stack.size() > 0) {

            if (stack.size() == 1) {
                lastIndex = stack.peek().getIndex();
            }
            System.out.print(lastIndex + "/" + firstSize  + " - " + stack.size() + "                                                                                \r");

            if (action == RecursiveAction.WENT_DEEPER || action == RecursiveAction.WENT_NEXT) {
                if (path.peek().getTargetId().equals(endId)) {
                    System.out.println("GEFUNDEN!");
                    System.out.println(path);

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

                    if (nextEdges == null || nextEdges.size() <= 0) {
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
                    action = RecursiveAction.WENT_BACK;
                }
                else {
                    path.push(next);
                    vertexPath.push(path.peek().getTargetId());
                    action = RecursiveAction.WENT_NEXT;
                }
            }
        }
    }
}
