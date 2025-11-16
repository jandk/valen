package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;
import org.jetbrains.annotations.*;

import java.nio.*;
import java.util.*;

@Debug.Renderer(
    childrenArray = "java.util.Arrays.copyOfRange(array, fromIndex, toIndex)"
)
public class Shorts implements Comparable<Shorts>, WrappedArray {
    private static final Shorts EMPTY = wrap(new short[0]);

    final short[] array;

    final int fromIndex;

    final int toIndex;

    Shorts(short[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public static Shorts empty() {
        return EMPTY;
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

    @Override
    public ShortBuffer asBuffer() {
        return ShortBuffer.wrap(array, fromIndex, size()).asReadOnlyBuffer();
    }

    public void copyTo(MutableShorts target, int offset) {
        System.arraycopy(array, fromIndex, target.array, target.fromIndex + offset, size());
    }

    public Shorts slice(int fromIndex) {
        return slice(fromIndex, size());
    }

    public Shorts slice(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, size());
        return new Shorts(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }

    @Override
    public int size() {
        return toIndex - fromIndex;
    }

    public boolean contains(short value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(short value) {
        for (int i = fromIndex; i < toIndex; i++) {
            if (array[i] == value) {
                return i - fromIndex;
            }
        }
        return -1;
    }

    public int lastIndexOf(short value) {
        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (array[i] == value) {
                return i - fromIndex;
            }
        }
        return -1;
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
        int result = 1;
        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + Short.hashCode(array[i]);
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
