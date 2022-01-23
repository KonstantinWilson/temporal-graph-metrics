package export;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * @deprecated
 */
public class RandomGraphGenerator {
    public final String[] vertices = {
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J"
    };
    private long minTime;
    private long maxTime;
    private String pathName;

    public RandomGraphGenerator(String pathname, long minTime, long maxTime) {
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.pathName = pathname;
    }

    public void export(int edgeAmount) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(pathName));

            for (int i = 0; i < edgeAmount; i++) {
                long timeA = (long)(minTime + Math.random() * (maxTime - minTime));
                long timeB = (long)(minTime + Math.random() * (maxTime - minTime));
                String line = new StringBuilder()
                        .append(vertices[(int)(Math.random() * vertices.length)])
                        .append("\t")
                        .append(Math.min(timeA, timeB))
                        .append("\t")
                        .append(vertices[(int)(Math.random() * vertices.length)])
                        .append("\t")
                        .append(Math.max(timeA, timeB))
                        .append("\t")
                        .toString();
                writer.append(line);
                writer.newLine();
            }
            writer.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        RandomGraphGenerator rgg = new RandomGraphGenerator("C:\\Users\\Konstantin\\Desktop\\graph.txt", 1, 48);
        rgg.export(50);
    }
}
