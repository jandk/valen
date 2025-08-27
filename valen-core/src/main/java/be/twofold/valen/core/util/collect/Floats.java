package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public class Floats extends AbstractList<Float> implements Comparable<Floats>, RandomAccess {
    final float[] array;

    final int fromIndex;

    final int toIndex;

    Floats(float[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public static Floats wrap(float[] array) {
        return new Floats(array, 0, array.length);
    }

    public static Floats wrap(float[] array, int fromIndex, int toIndex) {
        return new Floats(array, fromIndex, toIndex);
    }

    public static Floats from(FloatBuffer buffer) {
        Check.argument(buffer.hasArray(), "buffer must be backed by an array");
        return new Floats(buffer.array(), buffer.position(), buffer.limit());
    }

    public float getFloat(int index) {
        Check.index(index, size());
        return array[fromIndex + index];
    }

    public FloatBuffer asBuffer() {
        return FloatBuffer.wrap(array, fromIndex, size()).asReadOnlyBuffer();
    }

    public void copyTo(MutableFloats target, int offset) {
        System.arraycopy(array, fromIndex, target.array, target.fromIndex + offset, size());
    }

    public Floats slice(int fromIndex) {
        return subList(fromIndex, size());
    }

    public Floats slice(int fromIndex, int toIndex) {
        return subList(fromIndex, toIndex);
    }

    @Override
    public int size() {
        return toIndex - fromIndex;
    }

    @Override
    @Deprecated
    public Float get(int index) {
        return getFloat(index);
    }

    @Override
    public boolean contains(Object o) {
        return o instanceof java.lang.Float value && ArrayUtils.contains(array, fromIndex, toIndex, value);
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof java.lang.Float value) {
            int index = ArrayUtils.indexOf(array, fromIndex, toIndex, value);
            if (index >= 0) {
                return index - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o instanceof java.lang.Float value) {
            int index = ArrayUtils.lastIndexOf(array, fromIndex, toIndex, value);
            if (index >= 0) {
                return index - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public Floats subList(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, size());
        return new Floats(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }

    @Override
    public int compareTo(Floats o) {
        return Arrays.compare(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Floats o && Arrays.equals(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
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
