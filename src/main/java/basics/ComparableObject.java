package basics;

import java.util.ArrayList;

/**
 * Wrapper class to make objects comparable with a key.
 * @param <N> Type of the key
 * @param <O> Type of the object
 */
public class ComparableObject<N extends Number & Comparable, O extends Object> implements Comparable<ComparableObject<N, O>> {
    private N number;
    private O object;

    /**
     * Constructor of ComparableObject
     * @param number
     * @param object
     */
    public ComparableObject(N number, O object) {
        this.number = number;
        this.object = object;
    }

    @Override
    public int compareTo(ComparableObject<N, O> co) {
        return this.number.compareTo(co.number);
    }

    /**
     * Set the number/key.
     * @param number number of type N
     */
    public void setNumber(N number) {
        this.number = number;
    }

    /**
     * Returns the number/key.
     * @return number as N
     */
    public N getNumber() {
        return this.number;
    }

    /**
     * Sets the object.
     * @param object object of type O
     */
    public void setObject(O object) {
        this.object = object;
    }

    /**
     * Returns the object.
     * @return object of type O
     */
    public O getObject() {
        return this.object;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.number.toString())
                .append(" (")
                .append(this.object.toString())
                .append(")");
        return sb.toString();
    }
}
