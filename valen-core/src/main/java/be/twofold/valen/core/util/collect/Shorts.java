package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public class Shorts extends AbstractList<Short> implements Comparable<Shorts>, RandomAccess {
    final short[] array;

    final int fromIndex;

    final int toIndex;

    Shorts(short[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public static Shorts wrap(short[] array) {
        return new Shorts(array, 0, array.length);
    }

    public static Shorts wrap(short[] array, int fromIndex, int toIndex) {
        return new Shorts(array, fromIndex, toIndex);
    }

    public static Shorts from(ShortBuffer buffer) {
        Check.argument(buffer.hasArray(), "buffer must be backed by an array");
        return new Shorts(buffer.array(), buffer.position(), buffer.limit());
    }

    public short getShort(int index) {
        Check.index(index, size());
        return array[fromIndex + index];
    }

    public ShortBuffer asBuffer() {
        return ShortBuffer.wrap(array, fromIndex, size()).asReadOnlyBuffer();
    }

    public void copyTo(MutableShorts target, int offset) {
        System.arraycopy(array, fromIndex, target.array, target.fromIndex + offset, size());
    }

    public Shorts slice(int fromIndex) {
        return subList(fromIndex, size());
    }

    public Shorts slice(int fromIndex, int toIndex) {
        return subList(fromIndex, toIndex);
    }

    @Override
    public int size() {
        return toIndex - fromIndex;
    }

    @Override
    @Deprecated
    public Short get(int index) {
        return getShort(index);
    }

    @Override
    public boolean contains(Object o) {
        return o instanceof java.lang.Short value && ArrayUtils.contains(array, fromIndex, toIndex, value);
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof java.lang.Short value) {
            int index = ArrayUtils.indexOf(array, fromIndex, toIndex, value);
            if (index >= 0) {
                return index - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o instanceof java.lang.Short value) {
            int index = ArrayUtils.lastIndexOf(array, fromIndex, toIndex, value);
            if (index >= 0) {
                return index - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public Shorts subList(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, size());
        return new Shorts(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }

    @Override
    public int compareTo(Shorts o) {
        return Arrays.compare(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Shorts o && Arrays.equals(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
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
