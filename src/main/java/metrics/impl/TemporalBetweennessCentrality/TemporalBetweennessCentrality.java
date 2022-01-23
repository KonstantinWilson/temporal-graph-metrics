package metrics.impl.TemporalBetweennessCentrality;

import basics.ComparableObject;
import basics.StackItem;
import basics.diagram.DiagramV2;
import metrics.api.IMetric;
import metrics.impl.HopCount.RecursiveAction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;
import org.gradoop.temporal.model.impl.pojo.TemporalVertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Implementation of the Temporal Betweenness Centrality from "Graph Metrics for Temporal Networks" by Vincenzo Nicosia et. al.
 * The Temporal Betweenness Centrality says, how traversed a vertex is.
 * TBC = 1 / ((amountOfVertices-1) * (amountOfVertices-2)) * SUM( (amountOfShortestPaths(j,k) / amountOfShortestPathsThroughVertex(j,k)d) )
 */
public class TemporalBetweennessCentrality implements IMetric<Double> {
    private GradoopId vertexId;
    private List<TemporalVertex> vertices;
    private Double result = null;

    /**
     * Constructor of TemporalBetweennessCentrality
     * @param vertices All vertices of the graph.
     * @param vertexId Id of vertex, for which the metric shall be determined.
     */
    public TemporalBetweennessCentrality(List<TemporalVertex> vertices, GradoopId vertexId) {
        this.vertices = vertices;
        this.vertexId = vertexId;
    }

    @Override
    public void calculate(TemporalEdge edge) {
        // TODO Implement edge-by-edge/streaming calculation.
    }

    // 1 / ((N-1)*(N-2)) * SUMME(Anzahl kürzester Pfade/Anzahl kürzester Pfade durch Knoten)
    @Override
    public void calculate(List<TemporalEdge> edges) {
        double f1 = 1 / ((double)(vertices.size() - 1) * (double)(vertices.size() - 2));
        double f2 = 0;

        for (TemporalVertex sourceVertex: vertices) {
            for (TemporalVertex targetVertex: vertices) {
                if (!sourceVertex.getId().equals(targetVertex.getId())
                        && !sourceVertex.getId().equals(vertexId)
                        && !targetVertex.getId().equals(vertexId)
                ) {
                    List<GradoopId> path = new ArrayList<>();
                    path.add(sourceVertex.getId());
                    Tuple2<Long, Long> result = determine(edges, sourceVertex.getId(), targetVertex.getId());
                    if (result.f1 != 0) {
                        f2 += result.f0.doubleValue() / result.f1.doubleValue();
                    }
                }
            }
        }

        result = new Double(f1 * f2);
    }

    /**
     * Determines the Shortest Paths between two vertices.
     * @param edges Edges which shall be used to find the shortest paths.
     * @param startId Id of the origin vertex
     * @param endId Id of the destination vertex
     * @return Touple with two Long. The first number is the amount of shortest paths not traversing through vertexId. The second number is the amount of shortest paths traversing through vertexId.
     */
    private Tuple2<Long, Long> determine(List<TemporalEdge> edges, GradoopId startId, GradoopId endId) {
        Tuple2<Long, Long> frac = new Tuple2<>(new Long(0), new Long(0));

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
                    if (edgePath.contains(this.vertexId)) {
                        frac.f0++;
                    }
                    else {
                        frac.f1++;
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
        return frac;
    }

    @Override
    public DiagramV2<Long, Double> getData() {
        if (result == null) {
            return null;
        }
        else {
            DiagramV2 diagram = new DiagramV2(null);
            diagram.insertMin(0, 1, result);
            return  diagram;
        }
    }
}
