package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public final class MutableBytes extends Bytes {
    private MutableBytes(byte[] array, int offset, int length) {
        super(array, offset, length);
    }

    public static MutableBytes wrap(byte[] array) {
        return new MutableBytes(array, 0, array.length);
    }

    public static MutableBytes wrap(byte[] array, int offset, int length) {
        return new MutableBytes(array, offset, length);
    }

    public static MutableBytes allocate(int length) {
        return new MutableBytes(new byte[length], 0, length);
    }

    public MutableBytes set(int index, byte value) {
        Check.index(index, length);
        array[offset + index] = value;
        return this;
    }

    public MutableBytes setShort(int offset, short value) {
        Check.fromIndexSize(offset, Short.BYTES, length);
        VH_SHORT_LE.set(array, this.offset + offset, value);
        return this;
    }

    public MutableBytes setInt(int offset, int value) {
        Check.fromIndexSize(offset, Integer.BYTES, length);
        VH_INT_LE.set(array, this.offset + offset, value);
        return this;
    }

    public MutableBytes setLong(int offset, long value) {
        Check.fromIndexSize(offset, Long.BYTES, length);
        VH_LONG_LE.set(array, this.offset + offset, value);
        return this;
    }

    public MutableBytes setFloat(int offset, float value) {
        Check.fromIndexSize(offset, Float.BYTES, length);
        VH_FLOAT_LE.set(array, this.offset + offset, value);
        return this;
    }

    public MutableBytes setDouble(int offset, double value) {
        Check.fromIndexSize(offset, Double.BYTES, length);
        VH_DOUBLE_LE.set(array, this.offset + offset, value);
        return this;
    }

    public MutableBytes slice(int offset) {
        return slice(offset, length - offset);
    }

    public MutableBytes slice(int offset, int length) {
        Check.fromIndexSize(offset, length, this.length);
        return new MutableBytes(array, this.offset + offset, length);
    }

    public MutableBytes fill(byte value) {
        Arrays.fill(array, offset, offset + length, value);
        return this;
    }

    public ByteBuffer asMutableBuffer() {
        return ByteBuffer.wrap(array, offset, length);
    }
}
