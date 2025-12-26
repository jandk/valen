package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;
import java.util.stream.*;

public class Floats implements Slice, Comparable<Floats> {
    private static final Floats EMPTY = wrap(new float[0]);

    final float[] array;

    final int offset;

    final int length;

    Floats(float[] array, int offset, int length) {
        Check.fromIndexSize(offset, length, array.length);
        this.array = array;
        this.offset = offset;
        this.length = length;
    }

    public static Floats empty() {
        return EMPTY;
    }

    public static Floats wrap(float[] array) {
        return new Floats(array, 0, array.length);
    }

    public static Floats wrap(float[] array, int offset, int length) {
        return new Floats(array, offset, length);
    }

    public static Floats from(FloatBuffer buffer) {
        Check.argument(buffer.hasArray(), "buffer must be backed by an array");
        return new Floats(buffer.array(), buffer.position(), buffer.limit());
    }

    public float get(int index) {
        Check.index(index, length);
        return array[offset + index];
    }

    @Override
    public int length() {
        return length;
    }

    public boolean contains(float value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(float value) {
        for (int i = offset, limit = offset + length; i < limit; i++) {
            if (java.lang.Float.compare(array[i], value) == 0) {
                return i - offset;
            }
        }
        return -1;
    }

    public int lastIndexOf(float value) {
        for (int i = offset + length - 1; i >= offset; i--) {
            if (java.lang.Float.compare(array[i], value) == 0) {
                return i - offset;
            }
        }
        return -1;
    }

    public Floats slice(int offset) {
        return slice(offset, length - offset);
    }

    public Floats slice(int offset, int length) {
        Check.fromIndexSize(offset, length, this.length);
        return new Floats(array, this.offset + offset, length);
    }

    public void copyTo(Mutable target, int offset) {
        System.arraycopy(array, this.offset, target.array, target.offset + offset, length);
    }

    @Override
    public FloatBuffer asBuffer() {
        return FloatBuffer.wrap(array, offset, length).asReadOnlyBuffer();
    }

    public float[] toArray() {
        return Arrays.copyOfRange(array, offset, offset + length);
    }

    public DoubleStream stream() {
        return IntStream.range(offset, offset + length).mapToDouble(i -> array[i]);
    }

    @Override
    public int compareTo(Floats o) {
        return Arrays.compare(array, offset, offset + length, o.array, o.offset, o.offset + o.length);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Floats o && Arrays.equals(array, offset, offset + length, o.array, o.offset, o.offset + o.length);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = offset, limit = offset + length; i < limit; i++) {
            result = 31 * result + Float.hashCode(array[i]);
        }
        return result;
    }

    @Override
    public String toString() {
        return "[" + length + " floats]";
    }

    public static final class Mutable extends Floats {
        private Mutable(float[] array, int offset, int length) {
            super(array, offset, length);
        }

        public static Mutable wrap(float[] array) {
            return new Mutable(array, 0, array.length);
        }

        public static Mutable wrap(float[] array, int offset, int length) {
            return new Mutable(array, offset, length);
        }

        public static Mutable allocate(int length) {
            return new Mutable(new float[length], 0, length);
        }

        public Mutable set(int index, float value) {
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

        public Mutable fill(float value) {
            Arrays.fill(array, offset, offset + length, value);
            return this;
        }

        public FloatBuffer asMutableBuffer() {
            return FloatBuffer.wrap(array, offset, length);
        }
    }
}
