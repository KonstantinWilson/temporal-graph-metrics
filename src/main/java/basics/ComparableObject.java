package basics;

public class ComparableObject<N extends Number & Comparable, O extends Object> implements Comparable<N> {
    private N number;
    private O object;

    public ComparableObject(N number, O object) {
        this.number = number;
        this.object = object;
    }

    @Override
    public int compareTo(N n) {
        return this.number.compareTo(n);
    }

    public void setNumber(N number) {
        this.number = number;
    }

    public N getNumber() {
        return this.number;
    }

    public void setObject(O object) {
        this.object = object;
    }

    public O getObject() {
        return this.object;
    }
}
