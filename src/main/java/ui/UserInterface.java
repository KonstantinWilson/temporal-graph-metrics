package ui;

import basics.diagram.DiagramV2;
import export.ImageExporter;
import export.RandomGraphGenerator;
import importing.CSVImporter;
import importing.TestDataImporter;
import importing.api.IImporter;
import metrics.api.IMetric;
import metrics.impl.HopCount.HopCount;
import metrics.impl.TemporalBetweennessCentrality.TemporalBetweennessCentrality;
import metrics.impl.TemporalConnectedness.TemporalConnectedness;
import metrics.impl.TemporalShortestPath.TemporalShortestPath;
import org.apache.commons.cli.*;
import org.gradoop.common.model.impl.id.GradoopId;
import org.gradoop.temporal.model.impl.TemporalGraph;
import org.gradoop.temporal.model.impl.pojo.TemporalEdge;
import org.gradoop.temporal.model.impl.pojo.TemporalVertex;
import processor.TemporalGraphProcessor;
import processor.api.IMetricProcessor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserInterface {
    private IImporter importer = null;
    private IMetric metric = null;
    private TemporalGraphProcessor processor = null;

    public UserInterface() {

    }

    public void loadFile(String filepath) throws Exception {
        File file = null;
        try {
            file = new File(filepath);
        }
        catch (Exception e) {
            throw e;
        }
        CSVImporter csvImporter = new CSVImporter();
        csvImporter.load(file);
        this.importer = csvImporter;
    }

    public void loadTestData() {
        this.importer = new TestDataImporter();
    }

    public TemporalVertex getVertex(String label) {
        return this.importer.getVertices().stream().filter(v -> v.getLabel().equals(label)).findFirst().get();
    }

    public void setMetric(IMetric metric) {
        this.metric = metric;
    }

    public List<TemporalVertex> getVertices() {
        return importer.getVertices();
    }

    public List<TemporalEdge> getEdges() {
        return importer.getEdges();
    }

    public void process() {
        if (metric == null) {
            return;
        }

        this.processor = new TemporalGraphProcessor(this.metric);
        this.processor.process(this.importer.getEdges());
    }

    public DiagramV2 getData() {
        return processor.getData();
    }

    public static void main(String[] args) {
        UserInterface ui = new UserInterface();

        Options options = new Options();
        options.addOption("t", "test-data", false, "Run program with test data.");
        options.addOption("f", "file", true, "The file that should be loaded for processing.");
        options.addOption("o", "output", true, "The output file, where the result graph is saved. WARNING: Will be overwritten.");
        options.addOption("i", "image", true, "The file in which the graph shall be saved as an image, if possible.");

        CommandLine commandLine = null;
        CommandLineParser parser = new DefaultParser();
        try {
            commandLine = parser.parse(options, args);
        }
        catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            System.exit(0);
        }

        if (!commandLine.hasOption("o")) {
            System.out.println("You have to specify an output file with -o FILEPATH");

            System.exit(0);
        }


        if (!commandLine.hasOption("t") && commandLine.hasOption("f")) {
            try {
                System.out.println("Loading file ...");
                ui.loadFile(commandLine.getOptionValue("f"));
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }
        }
        else {
            System.out.println("Loading test data ...");
            ui.loadTestData();
        }
        System.out.println("... loaded " + ui.getVertices().size() + " vertices and " + ui.getEdges().size() + " edges.");

        System.out.println("Select metric:");
        System.out.println(" 1 - Hop Count");
        System.out.println(" 2 - Temporal Connectedness");
        System.out.println(" 3 - Temporal Shortest Path");
        System.out.println(" 4 - Temporal Betweenness Centrality");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Integer selection = null;
        do {
            selection = readInt("Select Option: ", true);
        } while (selection.intValue() < 1 || selection.intValue() > 4);

        System.out.println("Initializing metric ...");
        switch (selection.intValue()) {
            case 1: ui.setMetric(new HopCount(ui.getVertex("D").getId(), ui.getVertex("H").getId()));
                break;
            case 2: ui.setMetric(new TemporalConnectedness(ui.getVertex("E").getId(), ui.getVertex("J").getId()));
                break;
            case 3: ui.setMetric(new TemporalShortestPath(ui.getVertex("E").getId(), ui.getVertex("J").getId()));
                break;
            case 4: ui.setMetric(new TemporalBetweennessCentrality(ui.getVertices(), ui.getVertex("E").getId()));
                break;
        }

        System.out.println("Initializing metric processor ...");
        System.out.println("Processing graph/metric ...");
        ui.process();

        DiagramV2 result = ui.getData();
        if (result != null) {
            System.out.println("The result has " + result.getData().size() + " entries.");
            System.out.println(result.getData());

            if (commandLine.hasOption("o")) {
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(commandLine.getOptionValue("o")));
                    writer.write(result.getData().toString());
                    writer.flush();
                }
                catch (Exception e) {
                    System.out.println("Writing result in file failed: " + e.getMessage());
                }
                finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        }
                        catch (Exception e) { }
                    }
                }
            }

            if (commandLine.hasOption("i")) {
                ImageExporter exporter = new ImageExporter(512, 512, 16);
                exporter.draw(result);
                exporter.save(commandLine.getOptionValue("i"), true);
            }
        }
        else {
            System.out.println("Result is null.");
        }
    }

    public static String readString(String message, int minLength, boolean untilValid) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        do {
            System.out.print(message);
            try {
                input = reader.readLine();
            } catch (Exception e) {
                input = null;
            }
        } while ((untilValid && input == null) || (input != null && input.length() < minLength));
        return input;
    }

    public static Integer readInt(String message, boolean untilValid) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Integer i = null;
        do {
            System.out.print(message);
            try {
                String input = reader.readLine();
                i = Integer.valueOf(input);
            } catch (Exception e) {
                i = null;
            }
        } while (untilValid && i == null);
        return i;
    }

    public static TemporalGraph loadBikeGraph() {
        return null;
    }
}
