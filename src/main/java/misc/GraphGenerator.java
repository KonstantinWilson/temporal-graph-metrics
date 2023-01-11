package misc;

import org.apache.commons.io.FileExistsException;
import org.apache.flink.api.java.tuple.Tuple2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class GraphGenerator {
    private final String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private final String edgePrefix = "Edge ";
    private final int amountNodes;
    private final int amountEdges;
    private final List<String> nodes;

    public GraphGenerator(int amountNodes, int amountEdges) {
        this.amountNodes = amountNodes;
        this.amountEdges = amountEdges;
        nodes = generateNodes();
    }

    public String[] getNodes() {
        return this.nodes.toArray(new String[0]);
    }

    public void generateCSV(String pathname, long timespanMin, long timespanMax) throws Exception {
        File csv = new File(pathname);
        if (csv.exists()) {
            throw new FileExistsException();
        }
        try {
            if (!csv.createNewFile()) {
                throw new Exception("File couldn't be created.");
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(csv));
            writer.append("Source;ValidFrom;Target;ValidTo;Label");
            writer.newLine();
            writer.flush();
            TreeSet<Integer> usedNodes = new TreeSet<>();

            for (int i = 0; i < amountEdges; i ++) {
                int source = (int)(Math.random() * amountNodes);
                int target = (int)(Math.random() * amountNodes);
                Tuple2<Long, Long> validity = getTimespan(timespanMin, timespanMax);

                StringBuilder stringBuilder = new StringBuilder()
                        .append(nodes.get(source))
                        .append(";")
                        .append(validity.f0)
                        .append(";")
                        .append(nodes.get(target))
                        .append(";")
                        .append(validity.f1)
                        .append(";")
                        .append(edgePrefix + (i + 1));
                writer.append(stringBuilder.toString());
                writer.newLine();
                writer.flush();

                usedNodes.add(source);
                usedNodes.add(target);
            }
            writer.close();
            System.out.println(usedNodes.size() + " nodes were used.");
        }
        catch (Exception e) {
            throw e;
        }
    }

    private List<String> generateNodes() {
        List<String> nodes = new ArrayList<>();
        for (int i = 0; i < alphabet.length && nodes.size() < amountNodes; i++) {
            for (int j = 0; j < alphabet.length && nodes.size() < amountNodes; j++) {
                nodes.add(alphabet[i] + alphabet[j]);
            }
        }
        return nodes;
    }

    private Tuple2<Long, Long> getTimespan(long min, long max) {
        long value0;
        long value1;

        do {
            value0 = (long)(Math.random() * (max - min)) + min;
            value1 = (long)(Math.random() * (max - min)) + min;
        } while  (value0 == value1);

        return new Tuple2<Long, Long>(Math.min(value0, value1), Math.max(value0, value1));
    }

    public static void main(String[] args) {
        int nodes = 100;
        int[] edgeAmounts = {500, 1000, 5000, 10000, 50000, 100000, 500000, 1000000};
        int timespanMin = 0;
        int timespanMax = 250;
        for (int edgeAmount : edgeAmounts) {
            GraphGenerator generator = new GraphGenerator(nodes, edgeAmount);
            try {
                generator.generateCSV("testgraphs/" + nodes + "nodes" + edgeAmount + "edges.csv", timespanMin, timespanMax);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
