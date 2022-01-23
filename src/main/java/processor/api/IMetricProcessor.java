package processor.api;

import metrics.api.IMetric;
import org.gradoop.temporal.model.impl.TemporalGraph;

/**
 * Interface for implementation of metric processors.
 * @param <T> type of metric
 */
public interface IMetricProcessor<T extends  IMetric> {
    /**
     * Sets the metric.
     * @param metric metric of type IMetric
     */
    public void setMetric(IMetric metric);

    /**
     * Gets the metric.
     * @return metric of type IMetric
     */
    public IMetric getMetric();

    /**
     * Processes the metric for a temporal graph.
     * @param graph temporal graph to process
     */
    public void process(TemporalGraph graph);
}
