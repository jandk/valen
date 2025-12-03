package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;
import org.jetbrains.annotations.*;

import java.nio.*;
import java.util.*;

@Debug.Renderer(
    childrenArray = "java.util.Arrays.copyOfRange(array, fromIndex, toIndex)"
)
public class Ints implements Comparable<Ints>, Array {
    private static final Ints EMPTY = wrap(new int[0]);

    final int[] array;

    final int fromIndex;

    final int toIndex;

    Ints(int[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public static Ints empty() {
        return EMPTY;
    }

    public static Ints wrap(int[] array) {
        return new Ints(array, 0, array.length);
    }

    public static Ints wrap(int[] array, int fromIndex, int toIndex) {
        return new Ints(array, fromIndex, toIndex);
    }

    public static Ints from(IntBuffer buffer) {
        Check.argument(buffer.hasArray(), "buffer must be backed by an array");
        return new Ints(buffer.array(), buffer.position(), buffer.limit());
    }

    public int getInt(int index) {
        Check.index(index, length());
        return array[fromIndex + index];
    }

    @Override
    public IntBuffer asBuffer() {
        return IntBuffer.wrap(array, fromIndex, length()).asReadOnlyBuffer();
    }

    public void copyTo(MutableInts target, int offset) {
        System.arraycopy(array, fromIndex, target.array, target.fromIndex + offset, length());
    }

    public Ints slice(int fromIndex) {
        return slice(fromIndex, length());
    }

    public Ints slice(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, length());
        return new Ints(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }

    @Override
    public int length() {
        return toIndex - fromIndex;
    }

    public boolean contains(int value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(int value) {
        for (int i = fromIndex; i < toIndex; i++) {
            if (array[i] == value) {
                return i - fromIndex;
            }
        }
        return -1;
    }

    public int lastIndexOf(int value) {
        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (array[i] == value) {
                return i - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public int compareTo(Ints o) {
        return Arrays.compare(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Ints o && Arrays.equals(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + Integer.hashCode(array[i]);
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
