package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;
import org.jetbrains.annotations.*;

import java.nio.*;
import java.util.*;

@Debug.Renderer(
    childrenArray = "java.util.Arrays.copyOfRange(array, fromIndex, toIndex)"
)
public class Bytes implements Comparable<Bytes>, RandomAccess {
    private static final Bytes EMPTY = wrap(new byte[0]);

    final byte[] array;

    final int fromIndex;

    final int toIndex;

    Bytes(byte[] array, int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, array.length);
        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public static Bytes empty() {
        return EMPTY;
    }

    public static Bytes wrap(byte[] array) {
        return new Bytes(array, 0, array.length);
    }

    public static Bytes wrap(byte[] array, int fromIndex, int toIndex) {
        return new Bytes(array, fromIndex, toIndex);
    }

    public static Bytes from(ByteBuffer buffer) {
        Check.argument(buffer.hasArray(), "buffer must be backed by an array");
        return new Bytes(buffer.array(), buffer.position(), buffer.limit());
    }

    public byte getByte(int index) {
        Check.index(index, size());
        return array[fromIndex + index];
    }

    public short getShort(int offset) {
        Check.fromIndexSize(offset, Short.BYTES, size());
        return ByteArrays.getShort(array, fromIndex + offset, ByteOrder.LITTLE_ENDIAN);
    }

    public int getInt(int offset) {
        Check.fromIndexSize(offset, Integer.BYTES, size());
        return ByteArrays.getInt(array, fromIndex + offset, ByteOrder.LITTLE_ENDIAN);
    }

    public long getLong(int offset) {
        Check.fromIndexSize(offset, Long.BYTES, size());
        return ByteArrays.getLong(array, fromIndex + offset, ByteOrder.LITTLE_ENDIAN);
    }

    public float getFloat(int offset) {
        Check.fromIndexSize(offset, Float.BYTES, size());
        return ByteArrays.getFloat(array, fromIndex + offset, ByteOrder.LITTLE_ENDIAN);
    }

    public double getDouble(int offset) {
        Check.fromIndexSize(offset, Double.BYTES, size());
        return ByteArrays.getDouble(array, fromIndex + offset, ByteOrder.LITTLE_ENDIAN);
    }

    public int getUnsignedByte(int offset) {
        return Byte.toUnsignedInt(getByte(offset));
    }

    public int getUnsignedShort(int offset) {
        return Short.toUnsignedInt(getShort(offset));
    }

    public long getUnsignedInt(int offset) {
        return Integer.toUnsignedLong(getInt(offset));
    }

    public ByteBuffer asBuffer() {
        return ByteBuffer.wrap(array, fromIndex, size()).asReadOnlyBuffer();
    }

    public void copyTo(Bytes target, int offset) {
        System.arraycopy(array, fromIndex, target.array, target.fromIndex + offset, size());
    }

    public Bytes slice(int fromIndex) {
        return slice(fromIndex, size());
    }

    public Bytes slice(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, size());
        return new Bytes(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }

    public int size() {
        return toIndex - fromIndex;
    }

    public boolean contains(byte value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(byte value) {
        for (int i = fromIndex; i < toIndex; i++) {
            if (array[i] == value) {
                return i - fromIndex;
            }
        }
        return -1;
    }

    public int lastIndexOf(byte value) {
        for (int i = toIndex - 1; i >= fromIndex; i--) {
            if (array[i] == value) {
                return i - fromIndex;
            }
        }
        return -1;
    }

    @Override
    public int compareTo(Bytes o) {
        return Arrays.compare(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Bytes o && Arrays.equals(array, fromIndex, toIndex, o.array, o.fromIndex, o.toIndex);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = fromIndex; i < toIndex; i++) {
            result = 31 * result + Byte.hashCode(array[i]);
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
