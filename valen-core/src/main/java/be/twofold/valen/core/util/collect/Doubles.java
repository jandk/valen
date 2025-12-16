package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;
import org.jetbrains.annotations.*;

import java.nio.*;
import java.util.*;
import java.util.stream.*;

@Debug.Renderer(
    childrenArray = "java.util.Arrays.copyOfRange(array, offset, offset + length)"
)
public class Doubles implements Array, Comparable<Doubles> {
    private static final Doubles EMPTY = wrap(new double[0]);

    final double[] array;

    final int offset;

    final int length;

    Doubles(double[] array, int offset, int length) {
        Check.fromIndexSize(offset, length, array.length);
        this.array = array;
        this.offset = offset;
        this.length = length;
    }

    public static Doubles empty() {
        return EMPTY;
    }

    public static Doubles wrap(double[] array) {
        return new Doubles(array, 0, array.length);
    }

    public static Doubles wrap(double[] array, int offset, int length) {
        return new Doubles(array, offset, length);
    }

    public static Doubles from(DoubleBuffer buffer) {
        Check.argument(buffer.hasArray(), "buffer must be backed by an array");
        return new Doubles(buffer.array(), buffer.position(), buffer.limit());
    }

    public double get(int index) {
        Check.index(index, length);
        return array[offset + index];
    }

    @Override
    public int length() {
        return length;
    }

    public boolean contains(double value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(double value) {
        for (int i = offset, limit = offset + length; i < limit; i++) {
            if (java.lang.Double.compare(array[i], value) == 0) {
                return i - offset;
            }
        }
        return -1;
    }

    public int lastIndexOf(double value) {
        for (int i = offset + length - 1; i >= offset; i--) {
            if (java.lang.Double.compare(array[i], value) == 0) {
                return i - offset;
            }
        }
        return -1;
    }

    public Doubles slice(int offset) {
        return slice(offset, length - offset);
    }

    public Doubles slice(int offset, int length) {
        Check.fromIndexSize(offset, length, this.length);
        return new Doubles(array, this.offset + offset, length);
    }

    public void copyTo(MutableDoubles target, int offset) {
        System.arraycopy(array, this.offset, target.array, target.offset + offset, length);
    }

    @Override
    public DoubleBuffer asBuffer() {
        return DoubleBuffer.wrap(array, offset, length).asReadOnlyBuffer();
    }

    public double[] toArray() {
        return Arrays.copyOfRange(array, offset, offset + length);
    }

    public DoubleStream stream() {
        return Arrays.stream(array, offset, offset + length);
    }

    @Override
    public int compareTo(Doubles o) {
        return Arrays.compare(array, offset, offset + length, o.array, o.offset, o.offset + o.length);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Doubles o && Arrays.equals(array, offset, offset + length, o.array, o.offset, o.offset + o.length);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = offset, limit = offset + length; i < limit; i++) {
            result = 31 * result + Double.hashCode(array[i]);
        }
        return result;
    }

    @Override
    public String toString() {
        return "[" + length + " doubles]";
    }
}
