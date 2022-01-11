package importing;

import importing.api.IImporter;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.gradoop.common.model.api.entities.GraphHead;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.flink.model.api.layouts.LogicalGraphLayout;
import org.gradoop.flink.model.api.layouts.LogicalGraphLayoutFactory;
import org.gradoop.temporal.model.impl.TemporalGraph;
import org.gradoop.temporal.model.impl.TemporalGraphFactory;
import org.gradoop.temporal.model.impl.layout.TemporalGraphLayoutFactory;
import org.gradoop.temporal.model.impl.pojo.*;
import org.gradoop.temporal.util.TemporalGradoopConfig;
import scala.Int;

import java.io.*;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CSVImporter implements IImporter {
    public static final int TRIP_DURATION = 0;
    public static final int START_TIME = 1;
    public static final int STOP_TIME = 2;
    public static final int START_STATION_ID = 3;
    public static final int START_STATION_NAME = 4;
    public static final int START_STATION_LATITUDE = 5;
    public static final int START_STATION_LONGITUDE = 6;
    public static final int END_STATION_ID = 7;
    public static final int END_STATION_NAME = 8;
    public static final int END_STATION_LATITUDE = 9;
    public static final int END_STATION_LONGITUDE = 10;
    public static final int BIKE_ID = 11;
    public static final int USER_TYPE = 12;
    public static final int BIRTH_YEAR = 13;
    public static final int GENDER = 14;
    public static final String PROPERTY_LATITUDE = "Latitude";
    public static final String PROPERTY_LONGITUDE = "Longitude";
    public static final String PROPERTY_BIKE_ID = "BikeId";
    public static final String PROPERTY_USER_TYPE = "UserType";
    public static final String PROPERTY_BIRTH_YEAR = "BirthYear";
    public static final String PROPERTY_GENDER = "Gender";
    private BufferedReader reader = null;
    private HashMap<Integer, String> header = new HashMap();
    private ArrayList<TemporalVertex> vertices = new ArrayList<>();
    private ArrayList<TemporalEdge> edges = new ArrayList<>();
    private TemporalVertexFactory vertexFactory = new TemporalVertexFactory();
    private TemporalEdgeFactory edgeFactory = new TemporalEdgeFactory();

    public void load(File file) {
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            if (line != null) {
                this.parseHeader(reader.readLine(), ",");
            }

            long lineCount = 2;
            do {
                line = reader.readLine();
                if (line != null) {
                    try {
                        parseLine(line, ",");
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

//    public TemporalGraph getGraph() {
//        System.out.println("getGraph() 1");
//        System.out.println("this.edges.size() = " + this.edges.size());
//        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
//
//        TemporalGraphHead graphHead = new TemporalGraphHeadFactory().createGraphHead();
//        TemporalGraph graph = new TemporalGraphFactory(TemporalGradoopConfig.createConfig(env)).fromCollections(graphHead, this.vertices, this.edges);
//        System.out.println("getGraph() 2");
//        try {
//            DataSet<TemporalEdge> edges = graph.getEdges();
//            ArrayList<Tuple2<Integer, String>> tuples = new ArrayList<>();
//            tuples.add(new Tuple2<>(1, "eins"));
//            tuples.add(new Tuple2<>(2, "zwei"));
//            tuples.add(new Tuple2<>(3, "drei"));
//            DataSet<Tuple2<Integer, String>> newDS = env.fromCollection(tuples);
//            System.out.println("newDS.count() = " + newDS.count());
//        } catch (Exception e) {
//            System.out.println("Could not count: " + e.getMessage());
//            e.printStackTrace();
//        }
//        System.out.println("getGraph() 3");
//        return graph;
//    }

    public ArrayList<TemporalVertex> getVertices() {
        return this.vertices;
    }

    public ArrayList<TemporalEdge> getEdges() {
        return this.edges;
    }

    private void parseHeader(String line, String delim) {
        String[] fragments = line.split(delim);
        for (int i = 0; i < fragments.length; i++) {
            header.put(i, fragments[i]);
        }
    }

    private void parseLine(String line, String delim) {
        String[] fragments = line.split(delim);
        if (fragments[START_STATION_ID].contains("NULL") || fragments[END_STATION_ID].contains("NULL")) {
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Long sourceLongId = Long.valueOf(fragments[START_STATION_ID]);
        String sourceLabel = fragments[START_STATION_NAME];
        Double sourceLatitude = null;
        if (!fragments[START_STATION_LATITUDE].contains("NULL")) {
            sourceLatitude = Double.valueOf(fragments[START_STATION_LATITUDE]);
        }
        Double sourceLongitude = null;
        if (!fragments[START_STATION_LATITUDE].contains("NULL")) {
            sourceLongitude = Double.valueOf(fragments[START_STATION_LATITUDE]);
        }

        Long targetLongId = Long.valueOf(fragments[END_STATION_ID]);
        String targetLabel = fragments[END_STATION_NAME];
        Double targetLatitude = null;
        if (!fragments[END_STATION_LATITUDE].contains("NULL")) {
            targetLatitude = Double.valueOf(fragments[END_STATION_LATITUDE]);
        }
        Double targetLongitude = null;
        if (!fragments[END_STATION_LONGITUDE].contains("NULL")) {
            targetLongitude = Double.valueOf(fragments[END_STATION_LATITUDE]);
        }

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        try {
            startDate.setTime(simpleDateFormat.parse(fragments[START_TIME].replace("\"", "")));
            endDate.setTime(simpleDateFormat.parse(fragments[STOP_TIME].replace("\"", "")));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        long validFrom = startDate.getTimeInMillis() * 1000;
        long validTo = endDate.getTimeInMillis() * 1000;

        long bikeId = Long.parseLong(fragments[BIKE_ID]);
        String userType = fragments[USER_TYPE];

        Integer birthYear = null;
        if (!fragments[BIRTH_YEAR].contains("NULL")) {
            birthYear = Integer.valueOf(fragments[BIRTH_YEAR].replace("\"", ""));
        }
        short gender = Short.parseShort(fragments[GENDER]);

        TemporalVertex sourceVertex = null;
        for (Iterator<TemporalVertex> it = vertices.iterator(); it.hasNext() && sourceVertex == null;) {
            TemporalVertex next = it.next();
            if (next.getPropertyValue("Id").getLong() == sourceLongId) {
                sourceVertex = next;
            }
        }
        TemporalVertex targetVertex = null;
        for (Iterator<TemporalVertex> it = vertices.iterator(); it.hasNext() && targetVertex == null;) {
            TemporalVertex next = it.next();
            if (next.getPropertyValue("Id").getLong() == targetLongId) {
                targetVertex = next;
            }
        }

        if (sourceVertex == null) {
            TemporalVertex vertex = vertexFactory.createVertex(sourceLabel);
            vertex.setProperty("Id", sourceLongId);
            if (sourceLatitude != null) {
                vertex.setProperty(PROPERTY_LATITUDE, sourceLatitude);
            }
            if (sourceLongitude != null) {
                vertex.setProperty(PROPERTY_LONGITUDE, sourceLongitude);
            }
            this.vertices.add(vertex);
            sourceVertex = vertex;
        }
        if (targetVertex == null) {
            TemporalVertex vertex = vertexFactory.createVertex(targetLabel);
            vertex.setProperty("Id", targetLongId);
            if (targetLatitude != null) {
                vertex.setProperty(PROPERTY_LATITUDE, targetLatitude);
            }
            if (targetLongitude != null) {
                vertex.setProperty(PROPERTY_LONGITUDE, targetLongitude);
            }
            this.vertices.add(vertex);
            targetVertex = vertex;
        }

        try {
            TemporalEdge edge = edgeFactory.createEdge(sourceVertex.getId(), targetVertex.getId());
            edge.setValidTime(new Tuple2<>(validFrom, validTo));
            edge.setValidFrom(validFrom);
            edge.setValidTo(validTo);
            edge.setProperty(PROPERTY_BIKE_ID, bikeId);
            edge.setProperty(PROPERTY_USER_TYPE, userType);
            if (birthYear != null) {
                edge.setProperty(PROPERTY_BIRTH_YEAR, birthYear);
            }
            edge.setProperty(PROPERTY_GENDER, gender);
            this.edges.add(edge);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PrintStream Console = System.out;

        File file = null;
        if (args.length == 1) {
            try {
                file = new File(args[0]);
            }
            catch (Exception e) {
                Console.println("Invalid path in parameter.");
            }
        }
        else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter file path: ");
            do {
                try {
                    file = new File(reader.readLine());
                    if (!file.exists()) {
                        Console.println("The file doesn't exist.");
                        file = null;
                    }
                    else if (!file.isFile()) {
                        Console.println("The file is not a file.");
                        file = null;
                    }
                }
                catch (Exception e) {
                    Console.println("Something went wrong.");
                    file = null;
                }
            } while (file == null);
        }

        Console.println("Loading file ...");
        CSVImporter importer = new CSVImporter();
        importer.load(file);
        ArrayList<TemporalVertex> vertices = importer.getVertices();
        Console.println("Loaded " + vertices.size() + " vertices and " + importer.getEdges().size() + " edges.");
        // for (TemporalVertex vertex : vertices) {
        //     System.out.println(vertex.toString());
        // }
    }
}