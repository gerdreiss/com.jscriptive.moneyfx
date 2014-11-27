package com.jscriptive.moneyfx.model;

/**
* Created by Igor on 27/11/2014.
*/
public class ValueRange<T> {
    private T from;
    private T to;

    public ValueRange(T from, T to) {
        this.from = from;
        this.to = to;
    }

    public T from() {
        return from;
    }

    public boolean hasFrom() {
        return from() != null;
    }

    public T to() {
        return to;
    }

    public boolean hasTo() {
        return to() != null;
    }

    public boolean isEmpty() {
        return from() == null && to() == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValueRange)) return false;

        ValueRange that = (ValueRange) o;

        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        //noinspection RedundantIfStatement
        if (to != null ? !to.equals(that.to) : that.to != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("ValueRange{from=%s, to=%s}", from, to);
    }

    public String toPresentableString() {
        StringBuilder sb = new StringBuilder();
        if (hasFrom() && hasTo()) {
            sb.append("between ").append(from()).append(" and ").append(to());
        } else if (hasFrom()) {
            sb.append(" > ").append(from());
        } else if (hasTo()) {
            sb.append(" < ").append(to());
        }

        return sb.toString();
    }
}
