package export;

import basics.diagram.Diagram;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Saves a Diagram as a CSV-file.
 */
public class CSVExporter {
    private String filepath;

    public CSVExporter(String filepath) {
        this.filepath = filepath;
    }

    public <K extends Number, V extends Object> void save(Collection<K> keys, Collection<V> values) throws Exception {
        if (keys.size() != values.size()) {
            throw new IllegalArgumentException("keys and values must have the same length.");
        }
        try {
            File file = new File(filepath);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("Time;Metric");
            writer.newLine();

            Iterator<K> keyIterator = keys.iterator();
            Iterator<V> valueIterator = values.iterator();
            while (keyIterator.hasNext() && valueIterator.hasNext()) {
                writer.write(keyIterator.next().toString() + ";" + valueIterator.next());
                writer.newLine();
            }
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            throw e;
        }
    }

    public void save(Diagram diagram) throws Exception {
        try {
            File file = new File(filepath);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("Time;Metric");
            writer.newLine();

            Set<Number> keys = diagram.getData().keySet();
            for (Number k: keys) {
                writer.write(k.toString() + ";" + diagram.at(k));
                writer.newLine();
            }
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            throw e;
        }
    }
}
