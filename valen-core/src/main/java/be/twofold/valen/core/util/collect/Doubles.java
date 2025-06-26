package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.util.*;

public final class Doubles extends AbstractList<Double> implements Comparable<Doubles>, RandomAccess {
    private final double[] array;
    private final int fromIndex;
    private final int toIndex;

    private Doubles(double[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public static Doubles wrap(double[] array) {
        return new Doubles(array, 0, array.length);
    }

    public static Doubles wrap(double[] array, int fromIndex, int toIndex) {
        return new Doubles(array, fromIndex, toIndex);
    }

    public double getDouble(int index) {
        Check.index(index, size());
        return array[fromIndex + index];
    }


    @Override
    public int size() {
        return toIndex - fromIndex;
    }

    @Override
    @Deprecated
    public Double get(int index) {
        return getDouble(index);
    }

    @Override
    public boolean contains(Object o) {
        return o instanceof Double value
               && ArrayUtils.contains(array, fromIndex, toIndex, value);
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof Double value) {
            int index = ArrayUtils.indexOf(array, fromIndex, toIndex, value);
            if (index >= 0) {
                return index - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o instanceof Double value) {
            int index = ArrayUtils.lastIndexOf(array, fromIndex, toIndex, value);
            if (index >= 0) {
                return index - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public Doubles subList(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, size());
        return new Doubles(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }


    @Override
    public int compareTo(Doubles o) {
        return Arrays.compare(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Doubles o
               && Arrays.equals(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public int hashCode() {
        return ArrayUtils.hashCode(array, fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return ArrayUtils.toString(array, fromIndex, toIndex);
    }
}
