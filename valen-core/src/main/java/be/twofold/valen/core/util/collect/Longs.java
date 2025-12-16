package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;
import org.jetbrains.annotations.*;

import java.nio.*;
import java.util.*;
import java.util.stream.*;

@Debug.Renderer(
    childrenArray = "java.util.Arrays.copyOfRange(array, offset, offset + length)"
)
public class Longs implements Array, Comparable<Longs> {
    private static final Longs EMPTY = wrap(new long[0]);

    final long[] array;

    final int offset;

    final int length;

    Longs(long[] array, int offset, int length) {
        Check.fromIndexSize(offset, length, array.length);
        this.array = array;
        this.offset = offset;
        this.length = length;
    }

    public static Longs empty() {
        return EMPTY;
    }

    public static Longs wrap(long[] array) {
        return new Longs(array, 0, array.length);
    }

    public static Longs wrap(long[] array, int offset, int length) {
        return new Longs(array, offset, length);
    }

    public static Longs from(LongBuffer buffer) {
        Check.argument(buffer.hasArray(), "buffer must be backed by an array");
        return new Longs(buffer.array(), buffer.position(), buffer.limit());
    }

    public long get(int index) {
        Check.index(index, length);
        return array[offset + index];
    }

    @Override
    public int length() {
        return length;
    }

    public boolean contains(long value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(long value) {
        for (int i = offset, limit = offset + length; i < limit; i++) {
            if (array[i] == value) {
                return i - offset;
            }
        }
        return -1;
    }

    public int lastIndexOf(long value) {
        for (int i = offset + length - 1; i >= offset; i--) {
            if (array[i] == value) {
                return i - offset;
            }
        }
        return -1;
    }

    public Longs slice(int offset) {
        return slice(offset, length - offset);
    }

    public Longs slice(int offset, int length) {
        Check.fromIndexSize(offset, length, this.length);
        return new Longs(array, this.offset + offset, length);
    }

    public void copyTo(MutableLongs target, int offset) {
        System.arraycopy(array, this.offset, target.array, target.offset + offset, length);
    }

    @Override
    public LongBuffer asBuffer() {
        return LongBuffer.wrap(array, offset, length).asReadOnlyBuffer();
    }

    public long[] toArray() {
        return Arrays.copyOfRange(array, offset, offset + length);
    }

    public LongStream stream() {
        return Arrays.stream(array, offset, offset + length);
    }

    @Override
    public int compareTo(Longs o) {
        return Arrays.compare(array, offset, offset + length, o.array, o.offset, o.offset + o.length);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Longs o && Arrays.equals(array, offset, offset + length, o.array, o.offset, o.offset + o.length);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = offset, limit = offset + length; i < limit; i++) {
            result = 31 * result + Long.hashCode(array[i]);
        }
        return result;
    }

    @Override
    public String toString() {
        return "[" + length + " longs]";
    }
}
