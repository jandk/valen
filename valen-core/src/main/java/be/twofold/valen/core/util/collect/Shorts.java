package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;
import java.util.stream.*;

public class Shorts implements Slice, Comparable<Shorts> {
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

    public Shorts slice(int offset) {
        return slice(offset, length - offset);
    }

    public Shorts slice(int offset, int length) {
        Check.fromIndexSize(offset, length, this.length);
        return new Shorts(array, this.offset + offset, length);
    }

    public void copyTo(Mutable target, int offset) {
        System.arraycopy(array, this.offset, target.array, target.offset + offset, length);
    }

    @Override
    public ShortBuffer asBuffer() {
        return ShortBuffer.wrap(array, offset, length).asReadOnlyBuffer();
    }

    public short[] toArray() {
        return Arrays.copyOfRange(array, offset, offset + length);
    }

    public IntStream stream() {
        return IntStream.range(offset, offset + length).map(i -> array[i]);
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
        return "[" + length + " shorts]";
    }

    public static final class Mutable extends Shorts {
        private Mutable(short[] array, int offset, int length) {
            super(array, offset, length);
        }

        public static Mutable wrap(short[] array) {
            return new Mutable(array, 0, array.length);
        }

        public static Mutable wrap(short[] array, int offset, int length) {
            return new Mutable(array, offset, length);
        }

        public static Mutable allocate(int length) {
            return new Mutable(new short[length], 0, length);
        }

        public Mutable set(int index, short value) {
            Check.index(index, length);
            array[offset + index] = value;
            return this;
        }

        public Mutable slice(int offset) {
            return slice(offset, length - offset);
        }

        public Mutable slice(int offset, int length) {
            Check.fromIndexSize(offset, length, this.length);
            return new Mutable(array, this.offset + offset, length);
        }

        public Mutable fill(short value) {
            Arrays.fill(array, offset, offset + length, value);
            return this;
        }

        public ShortBuffer asMutableBuffer() {
            return ShortBuffer.wrap(array, offset, length);
        }
    }
}
