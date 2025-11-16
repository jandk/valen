package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;
import org.jetbrains.annotations.*;

import java.nio.*;
import java.util.*;

@Debug.Renderer(
    childrenArray = "java.util.Arrays.copyOfRange(array, fromIndex, toIndex)"
)
public class Doubles implements Comparable<Doubles>, WrappedArray {
    private static final Doubles EMPTY = wrap(new double[0]);

    final double[] array;

    final int fromIndex;

    final int toIndex;

    Doubles(double[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public static Doubles empty() {
        return EMPTY;
    }

    public static Doubles wrap(double[] array) {
        return new Doubles(array, 0, array.length);
    }

    public static Doubles wrap(double[] array, int fromIndex, int toIndex) {
        return new Doubles(array, fromIndex, toIndex);
    }

    public static Doubles from(DoubleBuffer buffer) {
        Check.argument(buffer.hasArray(), "buffer must be backed by an array");
        return new Doubles(buffer.array(), buffer.position(), buffer.limit());
    }

    public double getDouble(int index) {
        Check.index(index, size());
        return array[fromIndex + index];
    }

    @Override
    public DoubleBuffer asBuffer() {
        return DoubleBuffer.wrap(array, fromIndex, size()).asReadOnlyBuffer();
    }

    public void copyTo(MutableDoubles target, int offset) {
        System.arraycopy(array, fromIndex, target.array, target.fromIndex + offset, size());
    }

    public Doubles slice(int fromIndex) {
        return slice(fromIndex, size());
    }

    public Doubles slice(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, size());
        return new Doubles(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }

    @Override
    public int size() {
        return toIndex - fromIndex;
    }

    public boolean contains(double value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(double value) {
        for (int i = fromIndex; i < toIndex; i++) {
            if (Double.compare(array[i], value) == 0) {
                return i - fromIndex;
            }
        }
        return -1;
    }

    public int lastIndexOf(double value) {
        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (Double.compare(array[i], value) == 0) {
                return i - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public int compareTo(Doubles o) {
        return Arrays.compare(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Doubles o && Arrays.equals(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + Double.hashCode(array[i]);
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
