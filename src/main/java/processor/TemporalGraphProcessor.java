package processor;

import basics.diagram.DiagramV2;
import metrics.api.IMetric;
import org.apache.flink.api.java.DataSet;
import org.gradoop.temporal.model.impl.TemporalGraph;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;

import java.util.ArrayList;
import java.util.List;

public class TemporalGraphProcessor<T extends IMetric> {
    private T metric;
    public TemporalGraphProcessor(T metric) {
        this.metric = metric;
    }

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
    public void process(ArrayList<TemporalEdge> edges) {
        try {
            metric.calculate(edges);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DiagramV2 getData() {
        return metric.getData();
    }
}
