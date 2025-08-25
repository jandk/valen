package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;

public final class MutableLongs extends Longs {
    private MutableLongs(long[] array, int fromIndex, int toIndex) {
        super(array, fromIndex, toIndex);
    }

    public static MutableLongs wrap(long[] array) {
        return new MutableLongs(array, 0, array.length);
    }

    public static MutableLongs wrap(long[] array, int fromIndex, int toIndex) {
        return new MutableLongs(array, fromIndex, toIndex);
    }

    public static MutableLongs allocate(int size) {
        return new MutableLongs(new long[size], 0, size);
    }

    public void setLong(int index, long value) {
        Check.index(index, size());
        array[fromIndex + index] = value;
    }

    public LongBuffer asMutableBuffer() {
        return LongBuffer.wrap(array, fromIndex, size());
    }

    @Override
    public Long set(int index, Long element) {
        long oldValue = getLong(index);
        setLong(index, element);
        return oldValue;
    }
}
