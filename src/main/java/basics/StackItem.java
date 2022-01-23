package basics;

import java.util.List;

/**
 * StackItem used to implement Recursions iterativelly. Used to simulate a stack with a list.
 * @param <T> Data related to that StackItem.
 */
public class StackItem<T> {
    private List<T> elements;
    private long previousFrom;
    private long previousTo;
    private int index = 0;

    /**
     * Constructor for StackItem
     * @param elements List of elements for this "recursion" step to iterate through.
     * @param previousFrom Valid-from-time of the previous step.
     * @param previousTo Valid-to-time of the previous step.
     */
    public StackItem(List<T> elements, long previousFrom, long previousTo) {
        this.elements = elements;
        this.previousFrom = previousFrom;
        this.previousTo = previousTo;
    }

    /**
     * Jumps to the next element in the element list and returns it.
     * @return T if there is a next element and null if there isn't.
     */
    public T next() {
        if (index < elements.size()) {
            return elements.get(index++);
        }
        else {
            return null;
        }
    }

    /**
     * Returns the currently selected element.
     * @return T if there is a current element and null if there isn't.
     */
    public T current() {
        if (index < elements.size()) {
            return elements.get(index);
        }
        else {
            return null;
        }
    }

    /**
     * Returns the size (amount of) of elements.
     * @return Amount of elements as int.
     */
    public int size() {
        return elements.size();
    }

    /**
     * Gets the current index of the selcted element.
     * @return Index of elements as int.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the valid-from-time of the previous step.
     * @return Valid-from-time of the previous step as long.
     */
    public long getPreviousFrom() {
        return previousFrom;
    }

    /**
     * Returns the valid-to-time of the previous step.
     * @return Valid-to-time of the previous step as long.
     */
    public long getPreviousTo() {
        return previousTo;
    }
}
