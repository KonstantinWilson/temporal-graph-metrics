package metrics.api;

import basics.diagram.DiagramV2;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;

import java.util.List;

public interface IMetric<T extends /*Number &*/ Comparable> {
    /***
     * Determines the the metric for an edge and it's predecessors. Keeps all edges to work with them with the following edges from this method.
     * @param edge
     */
    public void calculate(TemporalEdge edge);

    /***
     * Determines the hop count for a list of edges, e.g. a whole graph.
     * @param edges
     */
    public void calculate(List<TemporalEdge> edges);
    public DiagramV2<Long, T> getData();
}
