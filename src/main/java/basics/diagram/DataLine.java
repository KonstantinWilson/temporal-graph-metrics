package basics.diagram;

public class DataLine<X extends Comparable, Y extends Comparable> {
    private X from;
    private boolean fromInclusive;
    private X to;
    private boolean toInclusive;
    private Y value;

    public DataLine(X from, boolean fromInclusive, X to, boolean toInclusive, Y value) {
        this.from = from;
        this.fromInclusive = fromInclusive;
        this.to = to;
        this.toInclusive = toInclusive;
        this.value = value;
    }

    public  DataLine(X from, X to, Y value) {
        this(from, true, to, false, value);
    }

    public void setFrom(X from) {
        this.from = from;
    }

    public X getFrom() {
        return this.from;
    }

    public void setTo(X to) {
        this.to = to;
    }

    public X getTo() {
        return this.to;
    }

    public void setRange(X from, X to) {
        this.from = from;
        this.to = to;
    }

    public void setValue(Y value) {
        this.value = value;
    }

    public Y getValue() {
        return this.value;
    }

    /**
     * -----this-----
     *  --line--
     * @param line
     * @return true if this wraps line.
     */
    public boolean wraps(DataLine<X, Y> line) {
        return  this.from.compareTo(line.getFrom()) <= 0 && this.to.compareTo(line.getTo()) >= 0;
    }

    /**
     *     ----this----
     *   --line--
     * @param line
     * @return true if line overlaps with this in the front area of this.
     */
    public boolean overlapsFront(DataLine<X, Y> line) {
        return this.from.compareTo(line.getFrom()) > 0 && this.from.compareTo(line.getTo()) < 0 && this.to.compareTo(line.getTo()) > 0;
    }

    /**
     *     ----this----
     *             --line--
     * @param line
     * @return true if line overlaps with this in the end area of this.
     */
    public boolean overlapsEnd(DataLine<X, Y> line) {
        return this.from.compareTo(line.getFrom()) < 0 && this.to.compareTo(line.getFrom()) > 0 && this.to.compareTo(line.getTo()) < 0;
    }

    @Override
    public String toString() {
        return "(" + this.from + " -> " + this.to + "; " + this.value + ")";
    }
}
