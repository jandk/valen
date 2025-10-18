package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;

public final class MutableBytes extends Bytes {
    private MutableBytes(byte[] array, int fromIndex, int toIndex) {
        super(array, fromIndex, toIndex);
    }

    public static MutableBytes wrap(byte[] array) {
        return new MutableBytes(array, 0, array.length);
    }

    public static MutableBytes wrap(byte[] array, int fromIndex, int toIndex) {
        return new MutableBytes(array, fromIndex, toIndex);
    }

    public static MutableBytes allocate(int size) {
        return new MutableBytes(new byte[size], 0, size);
    }

    public MutableBytes setByte(int index, byte value) {
        Check.index(index, size());
        array[fromIndex + index] = value;
        return this;
    }

    public MutableBytes setShort(int offset, short value) {
        Check.fromIndexSize(offset, Short.BYTES, size());
        ByteArrays.setShort(array, fromIndex + offset, value, ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public MutableBytes setInt(int offset, int value) {
        Check.fromIndexSize(offset, Integer.BYTES, size());
        ByteArrays.setInt(array, fromIndex + offset, value, ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public MutableBytes setLong(int offset, long value) {
        Check.fromIndexSize(offset, Long.BYTES, size());
        ByteArrays.setLong(array, fromIndex + offset, value, ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public MutableBytes setFloat(int offset, float value) {
        Check.fromIndexSize(offset, Float.BYTES, size());
        ByteArrays.setFloat(array, fromIndex + offset, value, ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public MutableBytes setDouble(int offset, double value) {
        Check.fromIndexSize(offset, Double.BYTES, size());
        ByteArrays.setDouble(array, fromIndex + offset, value, ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public ByteBuffer asMutableBuffer() {
        return ByteBuffer.wrap(array, fromIndex, size());
    }
}
