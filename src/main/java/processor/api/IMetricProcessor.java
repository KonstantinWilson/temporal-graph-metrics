package processor.api;

import metrics.api.IMetric;
import org.gradoop.temporal.model.impl.TemporalGraph;

public interface IMetricProcessor<T extends  IMetric> {
    public void setMetric(IMetric metric);
    public IMetric getMetric();
    public void process(TemporalGraph graph);
}
