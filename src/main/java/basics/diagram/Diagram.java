package basics.diagram;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @deprecated
 * @param <X>
 * @param <Y>
 */
public class Diagram<X extends Number & Comparable, Y extends Number & Comparable> {
    private ArrayList<DataLine<X, Y>> data;

    public Diagram() {
        data = new ArrayList<>();
    }

    public void setData(ArrayList<DataLine<X, Y>> data) {
        this.data = data;
    }

    public void insertMin(X from, X to, Y y) {
        DataLine<X, Y> newLine = new DataLine<>(from, to, y);
        List<DataLine<X, Y>> lines = data.stream().filter(d ->
                d.getTo().compareTo(from) > 0
                && d.getFrom().compareTo(to) < 0
        ).sorted((d1, d2) -> d1.getFrom().compareTo(d2.getFrom())).collect(Collectors.toList());

        if (lines.size() <= 0) {
            data.add(new DataLine<>(from, to, y));
        }
        else {
            X startPoint = null;
            X endPoint = null;
            ArrayList<DataLine<X, Y>> interruptions = new ArrayList<>();
            ArrayList<DataLine<X, Y>> toDelete = new ArrayList<>();
            for (DataLine<X, Y> line : lines) {
                boolean newValueSmaller = y.compareTo(line.getValue()) < 0;
                boolean newValueEqual = y.compareTo(line.getValue()) == 0;
                boolean newValueGreater = y.compareTo(line.getValue()) > 0;
                // ---------------
                if (newLine.overlapsFront(line)) {
                    if (newValueGreater) {
                        startPoint = line.getTo();
                    }
                    else if (newValueEqual) { // Falls die überschneidende Linie am Anfang den gleichen y-Wert hat, löschen und als Startpunkt setzen.
                        startPoint = line.getFrom();
                        toDelete.add(line);
                    }
                    else { // Andernfalls alte Linie kürzen und Startpunkt setzen.
                        startPoint = from;
                        line.setTo(from);
                    }
                }
                else if (newLine.overlapsEnd(line)) {
                    if (newValueGreater) {
                        endPoint = line.getFrom();
                    }
                    else if (newValueEqual) { // Falls die überschneidende Linie am Ende den gleichen y-Wert hat, löschen und als Endpunkt setzen.
                        endPoint = line.getTo();
                        toDelete.add(line);
                    }
                    else { // Andernfalls alte Linie kürzen und Endpunkt setzen.
                        startPoint = to;
                        line.setFrom(to);
                    }
                }
                else if (newLine.wraps(line)) {
                    if (newValueSmaller || newValueEqual) {
                        toDelete.add(line);
                    }
                    else {
                        interruptions.add(line);
                    }
                }
                else {
                    if (newValueSmaller) {
                        data.add(new DataLine<X, Y>(to, line.getTo(), line.getValue()));
                        line.setTo(from);
                        startPoint = from;
                        endPoint = to;
                    }
                }
            }

            data.removeAll(toDelete);
            if (startPoint == null) {
                startPoint = from;
            }
            if (endPoint == null) {
                endPoint = to;
            }

            if (startPoint != null && endPoint != null) {
                if (interruptions.size() <= 0) {
                    data.add(new DataLine<X, Y>(startPoint, endPoint, y));
                }
                else {
                    for (int i = 0; i < interruptions.size(); i++) {
                        DataLine<X, Y> interruption = interruptions.get(i);
                        if (i == 0) {
                            data.add(new DataLine<X, Y>(startPoint, interruption.getFrom(), y));
                        }
                        else {
                            data.add(new DataLine<X, Y>(interruptions.get(i-1).getTo(), interruption.getFrom(), y));
                        }
                        if (i == interruptions.size() - 1) {
                            data.add(new DataLine<X, Y>(interruption.getTo(), endPoint, y));
                        }
                    }
                }
            }
        }
    }

    public ArrayList<DataLine<X, Y>> getData() {
        return data;
    }

    public Y at(X x) {
        return null; // data.floorEntry(x).getValue();
    }

    public TreeMap<X, Y> at(X from, X to) {
        return null; // new TreeMap<>(data.subMap(from, true, to, true));
    }

    public static void main(String[] args) {
        Diagram<Long, Integer> diagram = new Diagram();
        diagram.insertMin(new Long(1),new Long(5), 1);
        diagram.insertMin(new Long(4),new Long(6), 3);
        diagram.insertMin(new Long(8),new Long(23), 7);
        diagram.insertMin(new Long(5),new Long(12), 2);

        ArrayList<DataLine<Long, Integer>> subGraph = diagram.getData();
        System.out.println(subGraph);
    }
}
