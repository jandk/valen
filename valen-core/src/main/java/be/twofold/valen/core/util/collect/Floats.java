package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public class Floats implements Comparable<Floats>, RandomAccess {
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
        return slice(fromIndex, size());
    }

    public Floats slice(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, size());
        return new Floats(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }

    public int size() {
        return toIndex - fromIndex;
    }

    public boolean contains(float value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(float value) {
        for (int i = fromIndex; i < toIndex; i++) {
            if (Float.compare(array[i], value) == 0) {
                return i - fromIndex;
            }
        }
        return -1;
    }

    public int lastIndexOf(float value) {
        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (Float.compare(array[i], value) == 0) {
                return i - fromIndex;
            }
        }
        return -1;
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
        int result = 1;
        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + Float.hashCode(array[i]);
        }
        return result;
    }

    @Override
    public String toString() {
        if (fromIndex == toIndex) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[').append(array[fromIndex]);
        for (int i = fromIndex + 1; i < toIndex; i++) {
            builder.append(", ").append(array[i]);
        }
        return builder.append(']').toString();
    }
}
