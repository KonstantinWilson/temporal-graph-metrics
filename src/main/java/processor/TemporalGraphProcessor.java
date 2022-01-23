package processor;

import basics.diagram.DiagramV2;
import metrics.api.IMetric;
import org.apache.flink.api.java.DataSet;
import org.gradoop.temporal.model.impl.TemporalGraph;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * MetricProcessor for temporal graphs.
 * @param <T> type of metric
 */
public class TemporalGraphProcessor<T extends IMetric> {
    private T metric;

    /**
     * Constructor of TemporalGraphProcessor
     * @param metric metric to process
     */
    public TemporalGraphProcessor(T metric) {
        this.metric = metric;
    }

    /**
     * Processes the metric for a temporal graph.
     * @param graph temporal graph to process
     */
    public void process(TemporalGraph graph) {
        DataSet<TemporalEdge> edges = graph.getEdges();
        try {
            List<TemporalEdge> lEdges = edges.collect();
            metric.calculate(lEdges);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes the metric for the edges of a temporal graph.
     * @param edges temporal edges to process
     */
    public void process(ArrayList<TemporalEdge> edges) {
        try {
            metric.calculate(edges);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the data/result of the processed metric.
     * @return metric result as DiagramV2
     */
    public DiagramV2 getData() {
        return metric.getData();
    }
}
