package importing;

import importing.api.IImporter;
import org.apache.flink.api.java.tuple.Tuple2;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;
import org.gradoop.temporal.model.impl.pojo.TemporalEdgeFactory;
import org.gradoop.temporal.model.impl.pojo.TemporalVertex;
import org.gradoop.temporal.model.impl.pojo.TemporalVertexFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class TestCSVLayout implements IImporter {
    public static final int SOURCE_NODE_LABEL = 0;
    public static final int VALID_FROM = 1;
    public static final int TARGET_NODE_LABEL = 2;
    public static final int VALID_TO = 3;
    public static final int EDGE_LABEL = 4;

    private BufferedReader reader = null;
    private HashMap<Integer, String> header = new HashMap();
    private ArrayList<TemporalVertex> vertices = new ArrayList<>();
    private ArrayList<TemporalEdge> edges = new ArrayList<>();
    private TemporalVertexFactory vertexFactory = new TemporalVertexFactory();
    private TemporalEdgeFactory edgeFactory = new TemporalEdgeFactory();

    /**
     * Reads the content of a file.
     * @param file File to read
     */
    public void load(File file) {
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            if (line != null) {
                this.parseHeader(line, ";");
            }

            long lineCount = 1;
            do {
                line = reader.readLine();
                if (line != null) {
                    try {
                        parseLine(line, ";");
                    }
                    catch (Exception e) {
                        System.out.println("Error in line " + lineCount + ".");
                        e.printStackTrace();
                    }
                }
                lineCount++;
            } while (line != null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<TemporalEdge> getEdges() {
        return edges;
    }

    @Override
    public ArrayList<TemporalVertex> getVertices() {
        return vertices;
    }

    /**
     * Parses the header of the cvs file.
     * @param line header line
     * @param delim delimiter of columns
     */
    private void parseHeader(String line, String delim) {
        String[] fragments = line.split(delim);
        for (int i = 0; i < fragments.length; i++) {
            header.put(i, fragments[i]);
        }
    }

    /**
     * Parses a non-header line of a cvs file.
     * @param line non-header line
     * @param delim delimiter of columns
     */
    private void parseLine(String line, String delim) {
        String[] fragments = line.split(delim);
        if (fragments.length != 5) {
            return;
        }

        String sourceLabel = fragments[SOURCE_NODE_LABEL];
        String targetLabel = fragments[TARGET_NODE_LABEL];
        String edgeLabel = fragments[EDGE_LABEL];
        String validFromStr = fragments[VALID_FROM];
        String validToStr = fragments[VALID_TO];

        TemporalVertex source = vertices.stream().filter(v -> v.getLabel().equals(sourceLabel)).findFirst().orElse(null);
        if (source == null) {
            source = vertexFactory.createVertex(sourceLabel);
            vertices.add(source);
        }
        TemporalVertex target = vertices.stream().filter(v -> v.getLabel().equals(targetLabel)).findFirst().orElse(null);
        if (target == null) {
            target = vertexFactory.createVertex(targetLabel);
            vertices.add(target);
        }

        TemporalEdge edge = edgeFactory.createEdge(edgeLabel, source.getId(), target.getId());
        edge.setValidFrom(Long.parseLong(validFromStr));
        edge.setValidTo(Long.parseLong(validToStr));
        edges.add(edge);
    }

    public static void main(String[] args) {
        TestCSVLayout layout = new TestCSVLayout();
        layout.load(new File("testgraphs/50nodes1000edges.csv"));

        System.out.println("Vertices: " + layout.getVertices().size());
        //for (TemporalVertex vertex : layout.getVertices()) {
        //    System.out.print(vertex.getLabel() + ", ");
        //}
        //System.out.println();

        System.out.println("Edges: " + layout.getEdges().size());
        //for (TemporalEdge edge : layout.getEdges()) {
        //    System.out.print(edge.getLabel() + ", ");
        //}
        //System.out.println();
    }
}
