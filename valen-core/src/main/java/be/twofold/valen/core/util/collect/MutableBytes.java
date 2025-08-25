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

    public static MutableBytes allocate(int length) {
        Check.argument(length >= 0, "length must not be negative");
        return wrap(new byte[length]);
    }

    public void setByte(int index, byte value) {
        Check.index(index, size());
        array[fromIndex + index] = value;
    }

    public ByteBuffer asMutableBuffer() {
        return ByteBuffer.wrap(array, fromIndex, size());
    }

    @Override
    public Byte set(int index, Byte element) {
        byte oldValue = getByte(index);
        setByte(index, element);
        return oldValue;
    }
}
