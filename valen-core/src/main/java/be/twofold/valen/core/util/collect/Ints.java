package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;
import org.jetbrains.annotations.*;

import java.nio.*;
import java.util.*;
import java.util.stream.*;

@Debug.Renderer(
    childrenArray = "java.util.Arrays.copyOfRange(array, offset, offset + length)"
)
public class Ints implements Comparable<Ints>, Array {
    private static final Ints EMPTY = wrap(new int[0]);

    final int[] array;

    final int offset;

    final int length;

    Ints(int[] array, int offset, int length) {
        Check.fromIndexSize(offset, length, array.length);
        this.array = array;
        this.offset = offset;
        this.length = length;
    }

    public static Ints empty() {
        return EMPTY;
    }

    public static Ints wrap(int[] array) {
        return new Ints(array, 0, array.length);
    }

    public static Ints wrap(int[] array, int offset, int length) {
        return new Ints(array, offset, length);
    }

    public static Ints from(IntBuffer buffer) {
        Check.argument(buffer.hasArray(), "buffer must be backed by an array");
        return new Ints(buffer.array(), buffer.position(), buffer.limit());
    }

    public int get(int index) {
        Check.index(index, length);
        return array[offset + index];
    }

    public long getUnsigned(int offset) {
        return Integer.toUnsignedLong(get(offset));
    }

    @Override
    public IntBuffer asBuffer() {
        return IntBuffer.wrap(array, offset, length).asReadOnlyBuffer();
    }

    public void copyTo(MutableInts target, int offset) {
        System.arraycopy(array, this.offset, target.array, target.offset + offset, length);
    }

    public Ints slice(int offset) {
        return slice(offset, length - offset);
    }

    public Ints slice(int offset, int length) {
        Check.fromIndexSize(offset, length, this.length);
        return new Ints(array, this.offset + offset, length);
    }

    public IntStream stream() {
        return Arrays.stream(array, offset, offset + length);
    }

    public int[] toArray() {
        return Arrays.copyOfRange(array, offset, offset + length);
    }

    @Override
    public int length() {
        return length;
    }

    public boolean contains(int value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(int value) {
        for (int i = offset, limit = offset + length; i < limit; i++) {
            if (array[i] == value) {
                return i - offset;
            }
        }
        return -1;
    }

    public int lastIndexOf(int value) {
        for (int i = offset + length - 1; i >= offset; i--) {
            if (array[i] == value) {
                return i - offset;
            }
        }
        return -1;
    }

    @Override
    public int compareTo(Ints o) {
        return Arrays.compare(array, offset, offset + length, o.array, o.offset, o.offset + o.length);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Ints o && Arrays.equals(array, offset, offset + length, o.array, o.offset, o.offset + o.length);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = offset, limit = offset + length; i < limit; i++) {
            result = 31 * result + Integer.hashCode(array[i]);
        }
        return result;
    }

    @Override
    public String toString() {
        if (length == 0) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[').append(array[offset]);
        for (int i = offset + 1, limit = offset + length; i < limit; i++) {
            builder.append(", ").append(array[i]);
        }
        return builder.append(']').toString();
    }
}
