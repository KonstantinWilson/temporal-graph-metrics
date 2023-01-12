package misc;

import importing.TestCSVLayout;
import metrics.impl.HopCount.HopCount;
import metrics.impl.TemporalBetweennessCentrality.TemporalBetweennessCentrality;
import metrics.impl.TemporalConnectedness.TemporalConnectedness;
import metrics.impl.TemporalShortestPath.TemporalShortestPath;
import org.apache.flink.api.java.tuple.Tuple2;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;
import org.gradoop.temporal.model.impl.pojo.TemporalVertex;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TimeMeasure {
    private int[][] pairs;
    private String genericFilepath;

    public TimeMeasure(String genericFilepath, int[][] nodeVertexPairs) {
        this.genericFilepath = genericFilepath;
        pairs = nodeVertexPairs;
    }

    public void run() {
        for (int[] pair : pairs) {
            String filepath = genericFilepath
                    .replace("[NODES]", pair[0] + "")
                    .replace("[EDGES]", pair[1] + "");
            File csv = new File(filepath);
            if (csv.exists()) {
                TestCSVLayout layout = new TestCSVLayout();
                layout.load(csv);
                List<TemporalVertex> nodes = layout.getVertices();
                List<TemporalEdge> edges = layout.getEdges();

                for (TemporalVertex node : nodes) {
                    System.out.print(node.getLabel() + ",");
                }
                System.out.println();

                TemporalVertex source = nodes.stream().filter(n -> n.getLabel().equals("AE")).findFirst().orElse(null);
                TemporalVertex target = nodes.stream().filter(n -> n.getLabel().equals("AG")).findFirst().orElse(null);

                Tuple2<Long, Integer> result = measureHopCount(source.getId(), target.getId(), edges);
                System.out.println(filepath + " took " + result.f0 + " nanoseconds and returned " + result.f1 + " results.");
            }
        }
    }

    private Tuple2<Long, Integer> measureHopCount(GradoopId sourceId, GradoopId targetId, List<TemporalEdge> edges) {
        HopCount metric = new HopCount(sourceId, targetId);

        long timeStart = System.nanoTime();
        metric.calculate(edges);
        long timeEnd = System.nanoTime();

        return new Tuple2<>(timeEnd - timeStart, metric.getData().getData().size());
    }

    private Tuple2<Long, Integer> measureTemporalConnectedness(GradoopId sourceId, GradoopId targetId, List<TemporalEdge> edges) {
        TemporalConnectedness metric = new TemporalConnectedness(sourceId, targetId);

        long timeStart = System.nanoTime();
        metric.calculate(edges);
        long timeEnd = System.nanoTime();

        return new Tuple2<>(timeEnd - timeStart, metric.getData().getData().size());
    }

    private Tuple2<Long, Integer> measureTemporalShortestPath(GradoopId sourceId, GradoopId targetId, List<TemporalEdge> edges) {
        TemporalShortestPath metric = new TemporalShortestPath(sourceId, targetId);

        long timeStart = System.nanoTime();
        metric.calculate(edges);
        long timeEnd = System.nanoTime();

        return new Tuple2<>(timeEnd - timeStart, metric.getData().getData().size());
    }

    private Tuple2<Long, Integer> measureTemporalBetweennessCentrality(List<TemporalVertex> nodes, List<TemporalEdge> edges, GradoopId targetId) {
        TemporalBetweennessCentrality metric = new TemporalBetweennessCentrality(nodes, targetId);

        long timeStart = System.nanoTime();
        metric.calculate(edges);
        long timeEnd = System.nanoTime();

        return new Tuple2<>(timeEnd - timeStart, metric.getData().getData().size());
    }

    public static void main(String[] args) {
        // 10 nodes -> AE and AG
        int[][] pairs = {
                {10, 50},
                {10, 100},
                {10, 500},
                {10, 1000},
                {10, 5000},
                //{10, 10000},
                //{10, 50000},
                //{10, 100000}
        };

        TimeMeasure tm = new TimeMeasure("testgraphs/[NODES]nodes[EDGES]edges.csv", pairs);
        tm.run();
    }
}
