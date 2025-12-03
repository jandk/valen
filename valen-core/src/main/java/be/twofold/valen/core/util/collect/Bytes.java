package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;
import org.jetbrains.annotations.*;

import java.lang.invoke.*;
import java.nio.*;
import java.util.*;

@Debug.Renderer(
    childrenArray = "java.util.Arrays.copyOfRange(array, fromIndex, toIndex)"
)
public class Bytes implements Comparable<Bytes>, Array {
    private static final Bytes EMPTY = wrap(new byte[0]);

    static final VarHandle VH_SHORT_LE = MethodHandles.byteArrayViewVarHandle(short[].class, ByteOrder.LITTLE_ENDIAN).withInvokeExactBehavior();

    static final VarHandle VH_INT_LE = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN).withInvokeExactBehavior();

    static final VarHandle VH_LONG_LE = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN).withInvokeExactBehavior();

    static final VarHandle VH_FLOAT_LE = MethodHandles.byteArrayViewVarHandle(float[].class, ByteOrder.LITTLE_ENDIAN).withInvokeExactBehavior();

    static final VarHandle VH_DOUBLE_LE = MethodHandles.byteArrayViewVarHandle(double[].class, ByteOrder.LITTLE_ENDIAN).withInvokeExactBehavior();

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
        Check.index(index, length());
        return array[fromIndex + index];
    }

    public short getShort(int offset) {
        Check.fromIndexSize(offset, Short.BYTES, length());
        return (short) VH_SHORT_LE.get(array, fromIndex + offset);
    }

    public int getInt(int offset) {
        Check.fromIndexSize(offset, Integer.BYTES, length());
        return (int) VH_INT_LE.get(array, fromIndex + offset);
    }

    public long getLong(int offset) {
        Check.fromIndexSize(offset, Long.BYTES, length());
        return (long) VH_LONG_LE.get(array, fromIndex + offset);
    }

    public float getFloat(int offset) {
        Check.fromIndexSize(offset, Float.BYTES, length());
        return (float) VH_FLOAT_LE.get(array, fromIndex + offset);
    }

    public double getDouble(int offset) {
        Check.fromIndexSize(offset, Double.BYTES, length());
        return (double) VH_DOUBLE_LE.get(array, fromIndex + offset);
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

    @Override
    public ByteBuffer asBuffer() {
        return ByteBuffer.wrap(array, fromIndex, length()).asReadOnlyBuffer();
    }

    public void copyTo(MutableBytes target, int offset) {
        System.arraycopy(array, fromIndex, target.array, target.fromIndex + offset, length());
    }

    public Bytes slice(int fromIndex) {
        return slice(fromIndex, length());
    }

    public Bytes slice(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, length());
        return new Bytes(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }

    @Override
    public int length() {
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
