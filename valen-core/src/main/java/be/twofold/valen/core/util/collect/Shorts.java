package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;
import org.jetbrains.annotations.*;

import java.nio.*;
import java.util.*;

@Debug.Renderer(
    childrenArray = "java.util.Arrays.copyOfRange(array, offset, offset + length)"
)
public class Shorts implements Comparable<Shorts>, Array {
    private static final Shorts EMPTY = wrap(new short[0]);

    final short[] array;

    final int offset;

    final int length;

    Shorts(short[] array, int offset, int length) {
        Check.fromIndexSize(offset, length, array.length);
        this.array = array;
        this.offset = offset;
        this.length = length;
    }

    public static Shorts empty() {
        return EMPTY;
    }

    public static Shorts wrap(short[] array) {
        return new Shorts(array, 0, array.length);
    }

    public static Shorts wrap(short[] array, int offset, int length) {
        return new Shorts(array, offset, length);
    }

    public static Shorts from(ShortBuffer buffer) {
        Check.argument(buffer.hasArray(), "buffer must be backed by an array");
        return new Shorts(buffer.array(), buffer.position(), buffer.limit());
    }

    public short get(int index) {
        Check.index(index, length);
        return array[offset + index];
    }

    public int getUnsigned(int offset) {
        return Short.toUnsignedInt(get(offset));
    }

    @Override
    public ShortBuffer asBuffer() {
        return ShortBuffer.wrap(array, offset, length).asReadOnlyBuffer();
    }

    public void copyTo(MutableShorts target, int offset) {
        System.arraycopy(array, this.offset, target.array, target.offset + offset, length);
    }

    public Shorts slice(int offset) {
        return slice(offset, length - offset);
    }

    public Shorts slice(int offset, int length) {
        Check.fromIndexSize(offset, length, this.length);
        return new Shorts(array, this.offset + offset, length);
    }

    @Override
    public int length() {
        return length;
    }

    public boolean contains(short value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(short value) {
        for (int i = offset, limit = offset + length; i < limit; i++) {
            if (array[i] == value) {
                return i - offset;
            }
        }
        return -1;
    }

    public int lastIndexOf(short value) {
        for (int i = offset + length - 1; i >= offset; i--) {
            if (array[i] == value) {
                return i - offset;
            }
        }
        return -1;
    }

    @Override
    public int compareTo(Shorts o) {
        return Arrays.compare(array, offset, offset + length, o.array, o.offset, o.offset + o.length);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Shorts o && Arrays.equals(array, offset, offset + length, o.array, o.offset, o.offset + o.length);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = offset, limit = offset + length; i < limit; i++) {
            result = 31 * result + Short.hashCode(array[i]);
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
