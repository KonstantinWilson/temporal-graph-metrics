package basics;

import java.util.ArrayList;
import java.util.List;

public class StackItem<T> {
    private List<T> elements;
    private long previousFrom;
    private long previousTo;
    private int index = 0;

    public StackItem(List<T> elements, long previousFrom, long previousTo) {
        this.elements = elements;
        this.previousFrom = previousFrom;
        this.previousTo = previousTo;
    }

    public T next() {
        if (index < elements.size()) {
            return elements.get(index++);
        }
        else {
            return null;
        }
    }

    public T current() {
        if (index < elements.size()) {
            return elements.get(index);
        }
        else {
            return null;
        }
    }

    public int size() {
        return elements.size();
    }

    public int getIndex() {
        return index;
    }

    public long getPreviousFrom() {
        return previousFrom;
    }

    public long getPreviousTo() {
        return previousTo;
    }
}
