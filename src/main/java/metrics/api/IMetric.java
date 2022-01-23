package metrics.api;

import basics.diagram.DiagramV2;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;

import java.util.List;

public interface IMetric<T extends /*Number &*/ Comparable> {
    /**
     * Determines the the metric for an edge and it's predecessors. Keeps all edges to work with them with the following edges from this method.
     * @param edge New edge
     */
    public void calculate(TemporalEdge edge);

    /**
     * Determines the hop count for a list of edges, e.g. a whole graph.
     * @param edges List of edges
     */
    public void calculate(List<TemporalEdge> edges);

    /**
     * Returns the result of the metric.
     * @return Result as a Diagram
     */
    public DiagramV2<Long, T> getData();
}
