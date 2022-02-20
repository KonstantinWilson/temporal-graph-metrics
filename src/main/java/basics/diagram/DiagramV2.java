package basics.diagram;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Diagram, that stores data with x as the time axis and y as the data axis.
 * @param <X> Time, has to be a comparable number.
 * @param <Y> Data, has to be comparable.
 */
public class DiagramV2<X extends Number & Comparable, Y extends Comparable> {
    private TreeMap<X, Y> data;
    private Y defaultValue;

    /**
     * Constructor for DiagramV2
     * @param defaultValue Default value for points with no data.
     */
    public DiagramV2(Y defaultValue) {
        data = new TreeMap<X, Y>();
        this.defaultValue = defaultValue;
    }

    /**
     * Replaces the data of the diagram.
     * @param data The data as a TreeMap
     */
    public void setData(TreeMap<X, Y> data) {
        this.data = data;
    }

    /**
     * Inserts a new value into the diagram. Lower values will be kept, higher values will be replaced.
     * @param from Starting time, from which the value will be valid.
     * @param to End time, to which the value will be valued.
     * @param newValue The new value to insert.
     */
    public void insertMin(X from, X to, Y newValue) {
        if (from.equals(to)) {
            throw new IllegalArgumentException("'from' and 'to' can't have the same value.");
        }
        if (from.compareTo(to) > 0) {
            throw new IllegalArgumentException("'from' can't be greater than 'to'.");
        }

        if (data.size() <= 0 || data.lastKey().compareTo(from) < 0 || data.firstKey().compareTo(to) > 0) {
            data.put(from, newValue);
            data.put(to, defaultValue);
        } else {
            X floorKeyFrom = data.floorKey(from);
            X floorKeyTo = data.floorKey(to);
            List<X> keys = data.keySet().stream().sorted().collect(Collectors.toList());
            Y lastValue = null;

            /*
                Neuer Datensatz unterbricht einen alten Datensatz:
                Alt:    o--------
                Neu:      o---
             */
            if (floorKeyFrom != null && floorKeyTo != null && floorKeyFrom.equals(floorKeyTo)) {
                Y oldValue = data.get(floorKeyFrom);
                if (newValue.compareTo(oldValue) < 0) {
                    data.put(from, newValue);
                    data.put(to, oldValue);
                }
            }
            else {
                if (floorKeyFrom == null) {
                    data.put(from, newValue);
                    lastValue = newValue;
                }
                for (X key : keys) {
                    if (key.equals(floorKeyFrom)) { // Anfang
                        Y value = data.get(key);
                        if (value == null || newValue.compareTo(value) < 0) {
                            data.put(from, newValue);
                            lastValue = newValue;
                        }
                        else {
                            lastValue = value;
                        }
                    }
                    else if (key.equals(floorKeyTo)) { // Ende
                        Y value = data.get(key);
                        if (value == null || newValue.compareTo(value) < 0) {
                            if (lastValue == newValue) {
                                data.remove(key);
                            }
                            else {
                                data.put(key, newValue);
                            }
                            data.put(to, value);
                        }
                        else if (value == null || newValue.compareTo(value) == 0) {
                            data.remove(key);
                        }
                    }
                    else if (key.compareTo(from) > 0 && key.compareTo(to) < 0) { // Mitte
                        Y value = data.get(key);
                        if (value == null || newValue.compareTo(value) <= 0) {
                            if (lastValue == newValue) {
                                data.remove(key);
                            }
                            else {
                                data.put(key, newValue);
                            }
                            lastValue = newValue;
                        }
                        else {
                            lastValue = value;
                        }
                    }
                }
            }
        }
    }

    /**
     * Inserts a new value into the diagram. Higher values will be kept, lower values will be replaced.
     * @param from Starting time, from which the value will be valid.
     * @param to End time, to which the value will be valued.
     * @param newValue The new value to insert.
     */
    public void insertMax(X from, X to, Y newValue) {
        if (from.equals(to)) {
            throw new IllegalArgumentException("'from' and 'to' can't have the same value.");
        }
        if (from.compareTo(to) > 0) {
            throw new IllegalArgumentException("'from' can't be greater than 'to'.");
        }

        if (data.size() <= 0 || data.lastKey().compareTo(from) < 0 || data.firstKey().compareTo(to) > 0) {
            data.put(from, newValue);
            data.put(to, defaultValue);
        } else {
            X floorKeyFrom = data.floorKey(from);
            X floorKeyTo = data.floorKey(to);
            List<X> keys = data.keySet().stream().sorted().collect(Collectors.toList());
            Y lastValue = null;

            /*
                Neuer Datensatz unterbricht einen alten Datensatz:
                Alt:    o--------
                Neu:      o---
             */
            if (floorKeyFrom != null && floorKeyTo != null && floorKeyFrom.equals(floorKeyTo)) {
                Y oldValue = data.get(floorKeyFrom);
                if (newValue.compareTo(oldValue) > 0) {
                    data.put(from, newValue);
                    data.put(to, oldValue);
                }
            }
            else {
                if (floorKeyFrom == null) {
                    data.put(from, newValue);
                    lastValue = newValue;
                }
                for (X key : keys) {
                    if (key.equals(floorKeyFrom)) { // Anfang
                        Y value = data.get(key);
                        if (value == null || newValue.compareTo(value) > 0) {
                            data.put(from, newValue);
                            lastValue = newValue;
                        }
                        else {
                            lastValue = value;
                        }
                    }
                    else if (key.equals(floorKeyTo)) { // Ende
                        Y value = data.get(key);
                        if (value == null || newValue.compareTo(value) > 0) {
                            if (lastValue == newValue) {
                                data.remove(key);
                            }
                            else {
                                data.put(key, newValue);
                            }
                            data.put(to, value);
                        }
                        else if (value == null || newValue.compareTo(value) == 0) {
                            data.remove(key);
                        }
                    }
                    else if (key.compareTo(from) > 0 && key.compareTo(to) < 0) { // Mitte
                        Y value = data.get(key);
                        if (value == null || newValue.compareTo(value) >= 0) {
                            if (lastValue == newValue) {
                                data.remove(key);
                            }
                            else {
                                data.put(key, newValue);
                            }
                            lastValue = newValue;
                        }
                        else {
                            lastValue = value;
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the diagram's data.
     * @return Data as a TreeMap.
     */
    public TreeMap<X, Y> getData() {
        return data;
    }

    /**
     * Returns the value at x.
     * @param x Point in time, at which the value shall be returned.
     * @return Value Y or default value.
     */
    public Y at(X x) {
        Map.Entry<X, Y> entry = this.data.floorEntry(x);
        return data.floorEntry(x) == null ? defaultValue : entry.getValue();
    }

    /**
     * Returns the values between from and to.
     * @param from Starting point of the return selection.
     * @param to End point of the return selection.
     * @return Values between from and to as TreeMap.
     */
    public TreeMap<X, Y> at(X from, X to) {
        return new TreeMap<X, Y>(data.subMap(from, true, to, true));
    }

    /**
     * Main method for testing.
     * @param args
     */
    public static void main(String[] args) {
        DiagramV2<Long, Integer> diagram = new DiagramV2(null);
//        diagram.insertMin(new Long(1),new Long(5), 1);
//        diagram.insertMin(new Long(4),new Long(15), 3); // 4 -> 6, 3
//        diagram.insertMin(new Long(8),new Long(23), 7);
//        diagram.insertMin(new Long(5),new Long(12), 2);

        diagram.insertMin(new Long(4),new Long(9), 3);
        diagram.insertMin(new Long(1),new Long(6), 5);

        TreeMap<Long, Integer> subGraph = diagram.getData();
        System.out.println(subGraph);
    }
}
