package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.lang.invoke.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

@Debug.Renderer(
    childrenArray = "java.util.Arrays.copyOfRange(array, offset, offset + length)"
)
public class Bytes implements Array, Comparable<Bytes> {
    private static final Bytes EMPTY = wrap(new byte[0]);

    static final VarHandle VH_SHORT_LE = MethodHandles.byteArrayViewVarHandle(short[].class, ByteOrder.LITTLE_ENDIAN).withInvokeExactBehavior();

    static final VarHandle VH_INT_LE = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN).withInvokeExactBehavior();

    static final VarHandle VH_LONG_LE = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN).withInvokeExactBehavior();

    static final VarHandle VH_FLOAT_LE = MethodHandles.byteArrayViewVarHandle(float[].class, ByteOrder.LITTLE_ENDIAN).withInvokeExactBehavior();

    static final VarHandle VH_DOUBLE_LE = MethodHandles.byteArrayViewVarHandle(double[].class, ByteOrder.LITTLE_ENDIAN).withInvokeExactBehavior();

    final byte[] array;

    final int offset;

    final int length;

    Bytes(byte[] array, int offset, int length) {
        Check.fromIndexSize(offset, length, array.length);
        this.array = array;
        this.offset = offset;
        this.length = length;
    }

    public static Bytes empty() {
        return EMPTY;
    }

    public static Bytes wrap(byte[] array) {
        return new Bytes(array, 0, array.length);
    }

    public static Bytes wrap(byte[] array, int offset, int length) {
        return new Bytes(array, offset, length);
    }

    public static Bytes from(ByteBuffer buffer) {
        Check.argument(buffer.hasArray(), "buffer must be backed by an array");
        return new Bytes(buffer.array(), buffer.position(), buffer.limit());
    }

    public byte get(int index) {
        Check.index(index, length);
        return array[offset + index];
    }

    public short getShort(int offset) {
        Check.fromIndexSize(offset, Short.BYTES, length);
        return (short) VH_SHORT_LE.get(array, this.offset + offset);
    }

    public int getInt(int offset) {
        Check.fromIndexSize(offset, Integer.BYTES, length);
        return (int) VH_INT_LE.get(array, this.offset + offset);
    }

    public long getLong(int offset) {
        Check.fromIndexSize(offset, Long.BYTES, length);
        return (long) VH_LONG_LE.get(array, this.offset + offset);
    }

    public float getFloat(int offset) {
        Check.fromIndexSize(offset, Float.BYTES, length);
        return (float) VH_FLOAT_LE.get(array, this.offset + offset);
    }

    public double getDouble(int offset) {
        Check.fromIndexSize(offset, Double.BYTES, length);
        return (double) VH_DOUBLE_LE.get(array, this.offset + offset);
    }

    public int getUnsigned(int offset) {
        return Byte.toUnsignedInt(get(offset));
    }

    public int getUnsignedShort(int offset) {
        return Short.toUnsignedInt(getShort(offset));
    }

    public long getUnsignedInt(int offset) {
        return Integer.toUnsignedLong(getInt(offset));
    }

    @Override
    public int length() {
        return length;
    }

    public boolean contains(byte value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(byte value) {
        for (int i = offset, limit = offset + length; i < limit; i++) {
            if (array[i] == value) {
                return i - offset;
            }
        }
        return -1;
    }

    public int lastIndexOf(byte value) {
        for (int i = offset + length - 1; i >= offset; i--) {
            if (array[i] == value) {
                return i - offset;
            }
        }
        return -1;
    }

    public Bytes slice(int offset) {
        return slice(offset, length - offset);
    }

    public Bytes slice(int offset, int length) {
        Check.fromIndexSize(offset, length, this.length);
        return new Bytes(array, this.offset + offset, length);
    }

    public void copyTo(MutableBytes target, int offset) {
        System.arraycopy(array, this.offset, target.array, target.offset + offset, length);
    }

    @Override
    public ByteBuffer asBuffer() {
        return ByteBuffer.wrap(array, offset, length).asReadOnlyBuffer();
    }

    public InputStream asInputStream() {
        return new ByteArrayInputStream(array, offset, length);
    }

    public byte[] toArray() {
        return Arrays.copyOfRange(array, offset, offset + length);
    }

    public String toHexString(HexFormat format) {
        return format.formatHex(array, offset, offset + length);
    }

    public String toString(Charset charset) {
        return new String(array, offset, length, charset);
    }

    public IntStream stream() {
        return IntStream.range(offset, offset + length).map(i -> array[i]);
    }

    @Override
    public int compareTo(Bytes o) {
        return Arrays.compare(array, offset, offset + length, o.array, o.offset, o.offset + o.length);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Bytes o && Arrays.equals(array, offset, offset + length, o.array, o.offset, o.offset + o.length);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = offset, limit = offset + length; i < limit; i++) {
            result = 31 * result + Byte.hashCode(array[i]);
        }
        return result;
    }

    @Override
    public String toString() {
        return "[" + length + " bytes]";
    }
}
