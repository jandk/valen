package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.util.*;

public class Bytes extends AbstractList<Byte> implements Comparable<Bytes>, RandomAccess {
    final byte[] array;
    final int fromIndex;
    final int toIndex;

    Bytes(byte[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public static Bytes wrap(byte[] array) {
        return new Bytes(array, 0, array.length);
    }

    public static Bytes wrap(byte[] array, int fromIndex, int toIndex) {
        return new Bytes(array, fromIndex, toIndex);
    }

    public byte getByte(int index) {
        Check.index(index, size());
        return array[fromIndex + index];
    }


    @Override
    public int size() {
        return toIndex - fromIndex;
    }

    @Override
    @Deprecated
    public Byte get(int index) {
        return getByte(index);
    }

    @Override
    public boolean contains(Object o) {
        return o instanceof Byte value
               && ArrayUtils.contains(array, fromIndex, toIndex, value);
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof Byte value) {
            int index = ArrayUtils.indexOf(array, fromIndex, toIndex, value);
            if (index >= 0) {
                return index - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o instanceof Byte value) {
            int index = ArrayUtils.lastIndexOf(array, fromIndex, toIndex, value);
            if (index >= 0) {
                return index - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public Bytes subList(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, size());
        return new Bytes(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }


    @Override
    public int compareTo(Bytes o) {
        return Arrays.compare(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Bytes o
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
