package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public class Longs extends AbstractList<Long> implements Comparable<Longs>, RandomAccess {
    final long[] array;
    final int fromIndex;
    final int toIndex;

    Longs(long[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public static Longs wrap(long[] array) {
        return new Longs(array, 0, array.length);
    }

    public static Longs wrap(long[] array, int fromIndex, int toIndex) {
        return new Longs(array, fromIndex, toIndex);
    }

    public static Longs allocate(int size) {
        Check.argument(size >= 0, "size must be non-negative");
        return new Longs(new long[size], 0, size);
    }

    public static Longs from(LongBuffer buffer) {
        Check.argument(buffer.hasArray(), "buffer must be backed by an array");
        return new Longs(buffer.array(), buffer.position(), buffer.limit());
    }

    public long getLong(int index) {
        Check.index(index, size());
        return array[fromIndex + index];
    }

    public LongBuffer asBuffer() {
        return LongBuffer.wrap(array, fromIndex, size()).asReadOnlyBuffer();
    }

    public void copyTo(MutableLongs target, int offset) {
        System.arraycopy(array, fromIndex, target.array, target.fromIndex + offset, size());
    }

    public Longs slice(int fromIndex) {
        return subList(fromIndex, size());
    }

    public Longs slice(int fromIndex, int toIndex) {
        return subList(fromIndex, toIndex);
    }


    @Override
    public int size() {
        return toIndex - fromIndex;
    }

    @Override
    @Deprecated
    public Long get(int index) {
        return getLong(index);
    }

    @Override
    public boolean contains(Object o) {
        return o instanceof Long value
               && ArrayUtils.contains(array, fromIndex, toIndex, value);
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof Long value) {
            int index = ArrayUtils.indexOf(array, fromIndex, toIndex, value);
            if (index >= 0) {
                return index - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o instanceof Long value) {
            int index = ArrayUtils.lastIndexOf(array, fromIndex, toIndex, value);
            if (index >= 0) {
                return index - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public Longs subList(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, size());
        return new Longs(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }


    @Override
    public int compareTo(Longs o) {
        return Arrays.compare(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Longs o
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
