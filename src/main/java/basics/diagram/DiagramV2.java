package basics.diagram;

import java.util.*;
import java.util.stream.Collectors;

public class DiagramV2<X extends Number & Comparable, Y extends /*Number &*/ Comparable> {
    private TreeMap<X, Y> data;
    private Y defaultValue;

    public DiagramV2(Y defaultValue) {
        data = new TreeMap<X, Y>();
        this.defaultValue = defaultValue;
    }

    public void setData(TreeMap<X, Y> data) {
        this.data = data;
    }

    public void insertMin(X from, X to, Y newValue) {
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
                    }
                    else if (key.compareTo(from) > 0 && key.compareTo(to) < 0) { // Mitte
                        Y value = data.get(key);
                        if (value == null || newValue.compareTo(value) < 0) {
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

    public void insertMax(X from, X to, Y newValue) {
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
                    }
                    else if (key.compareTo(from) > 0 && key.compareTo(to) < 0) { // Mitte
                        Y value = data.get(key);
                        if (value == null || newValue.compareTo(value) > 0) {
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

    public TreeMap<X, Y> getData() {
        return data;
    }

    public Y at(X x) {
        Map.Entry<X, Y> entry = this.data.floorEntry(x);
        return data.floorEntry(x) == null ? defaultValue : entry.getValue();
    }

    public TreeMap<X, Y> at(X from, X to) {
        return new TreeMap<X, Y>(data.subMap(from, true, to, true));
    }

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
