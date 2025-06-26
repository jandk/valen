package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

public final class MutableInts extends Ints {
    private MutableInts(int[] array, int fromIndex, int toIndex) {
        super(array, fromIndex, toIndex);
    }

    public static MutableInts wrap(int[] array) {
        return new MutableInts(array, 0, array.length);
    }

    public static MutableInts wrap(int[] array, int fromIndex, int toIndex) {
        return new MutableInts(array, fromIndex, toIndex);
    }

    public void setInt(int index, int value) {
        Check.index(index, size());
        array[fromIndex + index] = value;
    }

    @Override
    public Integer set(int index, Integer element) {
        int oldValue = getInt(index);
        setInt(index, element);
        return oldValue;
    }
}
