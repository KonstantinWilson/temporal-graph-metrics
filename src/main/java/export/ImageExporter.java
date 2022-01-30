package export;

import basics.diagram.DiagramV2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Image Exporter saves a diagram as a png file.
 * @param <X>
 * @param <Y>
 */
public class ImageExporter<X extends Number & Comparable, Y extends Number & Comparable> {
    private BufferedImage image;
    private Graphics2D graphics;
    private final int width;
    private final int height;
    private final int margin;
    private final int maxX;
    private final int maxContentX;
    private final int maxY;
    private final int maxContentY;

    private X diagramXMin;
    private X diagramXMax;
    private long diagramWidth;
    private Y diagramYMin;
    private Y diagramYMax;
    private long diagramHeight;

    /**
     * Constructor of ImageExporter
     * @param width Preferred width of the diagram in the image.
     * @param height Preferred height of the diagram in the image.
     * @param margin Margin between the diagram and the edges of the image.
     */
    public ImageExporter(int width, int height, int margin) {
        this.width = width;
        this.height = height;
        this.margin = margin;

        this.maxX = width - 2 * margin;
        this.maxContentX = width - 3 * margin;
        this.maxY = height - 2 * margin;
        this.maxContentY = height - 3 * margin;
    }

    /**
     * Renders a DiagramV2
     * @param diagram
     */
    public void draw(DiagramV2<X, Y> diagram) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.graphics = (Graphics2D)image.getGraphics();
        retrieveExtremes(diagram);

        clear();
        drawCS();
        drawContent(diagram);
    }

    /**
     * Saves the rendered image as a png file.
     * @param pathname Destination path and file name.
     * @param overwrite If an existing file with the same name should be overwritten.
     * @return true if the saving was successfull, otherwise false.
     */
    public boolean save(String pathname, boolean overwrite) {
        File file = new File(pathname);

        if (file.exists() && !overwrite) {
            return false;
        }

        try {
            ImageIO.write(image, "png", file);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Retrieves the lowest and highest values of the x and y axis of the diagram.
     * @param diagram
     */
    private void retrieveExtremes(DiagramV2<X, Y> diagram) {
        if (diagram == null || diagram.getData() == null || diagram.getData().size() == 0) {
            return;
        }
        TreeMap<X, Y> data = diagram.getData();

        diagramXMin = data.firstKey();
        diagramXMax = data.lastKey();
        diagramWidth = diagramXMax.longValue() - diagramXMin.longValue();
        List<Y> values = data.values().stream().filter(v -> v != null).collect(Collectors.toList());
        diagramYMin = values.stream().min(Comparable::compareTo).get();
        diagramYMax = values.stream().max(Comparable::compareTo).get();
        diagramHeight = diagramYMax.longValue() - diagramYMin.longValue();
    }

    /**
     * Clears the currently rendered image.
     */
    private void clear() {
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, width, height);
    }

    /**
     * Renders the coordinate system's x- and y-axis, arrows and labels.
     */
    private void drawCS() {
        graphics.setColor(Color.BLACK);
        // Arrow Y
        graphics.drawLine(x(0), y(0), x(0), y(maxY));
        graphics.drawLine(x(-8), y(maxY-8), x(0), y(maxY));
        graphics.drawLine(x(8), y(maxY-8), x(0), y(maxY));
        // Label Y
        graphics.drawString(diagramYMax + "", x(-margin), y(maxContentY));
        graphics.drawString(diagramYMin + "", x(-margin), y(0));

        // Arrow X
        graphics.drawLine(x(0), y(0), x(maxX), y(0));
        graphics.drawLine(x(maxX-8), y(8), x(maxX), y(0));
        graphics.drawLine(x(maxX-8), y(-8), x(maxX), y(0));
        // Label X
        graphics.drawString(diagramXMax + "", x(maxContentX), y(-margin));
        graphics.drawString(diagramXMin + "", x(0), y(-margin));
    }

    /**
     * Renders the content of the diagram.
     * @param diagram
     */
    private void drawContent(DiagramV2<X, Y> diagram) {
        double segmentSizeX = (double)maxContentX / (double)diagramWidth;
        double segmentSizeY = (double)maxContentY / (double)diagramHeight;

        X from = null;
        X to = null;
        Y value = null;

        graphics.setColor(Color.RED);
        for (Map.Entry<X, Y> entry : diagram.getData().entrySet()) {
            to = entry.getKey();

            if (from != null && to != null && value != null) {
                int y = (int)Math.round((value.doubleValue() - diagramYMin.doubleValue()) * segmentSizeY);
                drawLine(
                        (int)Math.round((from.doubleValue() - diagramXMin.doubleValue()) * segmentSizeX),
                        y,
                        (int)Math.round((to.doubleValue() - diagramXMin.doubleValue()) * segmentSizeX),
                        y
                );
            }

            from = entry.getKey();
            value = entry.getValue();
        }
    }

    /**
     * Transforms x from diagram coordinates to image coordinates.
     * @param value
     * @return transformed value as int.
     */
    private int x(int value) {
        return value + margin;
    }

    /**
     * Transforms y from diagram coordinates to image coordinates.
     * @param value
     * @return transformed value as int.
     */
    private int y(int value) {
        return height - (value + margin);
    }

    /**
     * Renders a line. Expects diagram coordinates.
     * @param x1 x-coordinate of the starting point
     * @param y1 y-coordinate of the starting point
     * @param x2 x-coordinate of the end point
     * @param y2 y-coordinate of the end point
     */
    private void drawLine(int x1, int y1, int x2, int y2) {
        graphics.drawLine(x(x1), y(y1), x(x2), y(y2));
    }

    /**
     * Main class for testing.
     * @param args
     */
    public static void main(String[] args) {
        ImageExporter ie = new ImageExporter(512, 512, 16);
        DiagramV2<Long, Integer> diagram = new DiagramV2(null);
        diagram.insertMin(new Long(1),new Long(5), 1);
        diagram.insertMin(new Long(4),new Long(15), 3); // 4 -> 6, 3
        diagram.insertMin(new Long(8),new Long(23), 7);
        diagram.insertMin(new Long(5),new Long(12), 2);
        ie.draw(diagram);
        ie.save("C:\\Users\\Konstantin\\Desktop\\image.png", true);
    }
}
