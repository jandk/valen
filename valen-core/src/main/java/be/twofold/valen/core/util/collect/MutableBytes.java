package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

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

    public void setByte(int index, byte value) {
        Check.index(index, size());
        array[fromIndex + index] = value;
    }

    @Override
    public Byte set(int index, Byte element) {
        byte oldValue = getByte(index);
        setByte(index, element);
        return oldValue;
    }
}
