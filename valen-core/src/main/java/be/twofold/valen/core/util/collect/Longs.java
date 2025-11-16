package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;
import org.jetbrains.annotations.*;

import java.nio.*;
import java.util.*;

@Debug.Renderer(
    childrenArray = "java.util.Arrays.copyOfRange(array, fromIndex, toIndex)"
)
public class Longs implements Comparable<Longs>, WrappedArray {
    private static final Longs EMPTY = wrap(new long[0]);

    final long[] array;

    final int fromIndex;

    final int toIndex;

    Longs(long[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public static Longs empty() {
        return EMPTY;
    }

    public static Longs wrap(long[] array) {
        return new Longs(array, 0, array.length);
    }

    public static Longs wrap(long[] array, int fromIndex, int toIndex) {
        return new Longs(array, fromIndex, toIndex);
    }

    public static Longs from(LongBuffer buffer) {
        Check.argument(buffer.hasArray(), "buffer must be backed by an array");
        return new Longs(buffer.array(), buffer.position(), buffer.limit());
    }

    public long getLong(int index) {
        Check.index(index, size());
        return array[fromIndex + index];
    }

    @Override
    public LongBuffer asBuffer() {
        return LongBuffer.wrap(array, fromIndex, size()).asReadOnlyBuffer();
    }

    public void copyTo(MutableLongs target, int offset) {
        System.arraycopy(array, fromIndex, target.array, target.fromIndex + offset, size());
    }

    public Longs slice(int fromIndex) {
        return slice(fromIndex, size());
    }

    public Longs slice(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, size());
        return new Longs(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }

    @Override
    public int size() {
        return toIndex - fromIndex;
    }

    public boolean contains(long value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(long value) {
        for (int i = fromIndex; i < toIndex; i++) {
            if (array[i] == value) {
                return i - fromIndex;
            }
        }
        return -1;
    }

    public int lastIndexOf(long value) {
        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (array[i] == value) {
                return i - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public int compareTo(Longs o) {
        return Arrays.compare(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Longs o && Arrays.equals(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + Long.hashCode(array[i]);
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
