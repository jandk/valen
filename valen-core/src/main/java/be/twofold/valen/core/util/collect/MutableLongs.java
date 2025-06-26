package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

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

    public void setLong(int index, long value) {
        Check.index(index, size());
        array[fromIndex + index] = value;
    }

    @Override
    public Long set(int index, Long element) {
        long oldValue = getLong(index);
        setLong(index, element);
        return oldValue;
    }
}
