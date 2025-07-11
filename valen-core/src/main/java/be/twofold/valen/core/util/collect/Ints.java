package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.util.*;

public class Ints extends AbstractList<Integer> implements Comparable<Ints>, RandomAccess {
    final int[] array;
    final int fromIndex;
    final int toIndex;

    Ints(int[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public static Ints wrap(int[] array) {
        return new Ints(array, 0, array.length);
    }

    public static Ints wrap(int[] array, int fromIndex, int toIndex) {
        return new Ints(array, fromIndex, toIndex);
    }

    public int getInt(int index) {
        Check.index(index, size());
        return array[fromIndex + index];
    }


    @Override
    public int size() {
        return toIndex - fromIndex;
    }

    @Override
    @Deprecated
    public Integer get(int index) {
        return getInt(index);
    }

    @Override
    public boolean contains(Object o) {
        return o instanceof Integer value
               && ArrayUtils.contains(array, fromIndex, toIndex, value);
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof Integer value) {
            int index = ArrayUtils.indexOf(array, fromIndex, toIndex, value);
            if (index >= 0) {
                return index - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o instanceof Integer value) {
            int index = ArrayUtils.lastIndexOf(array, fromIndex, toIndex, value);
            if (index >= 0) {
                return index - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public Ints subList(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, size());
        return new Ints(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }


    @Override
    public int compareTo(Ints o) {
        return Arrays.compare(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Ints o
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
