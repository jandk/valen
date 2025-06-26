package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

public final class MutableShorts extends Shorts {
    private MutableShorts(short[] array, int fromIndex, int toIndex) {
        super(array, fromIndex, toIndex);
    }

    public static MutableShorts wrap(short[] array) {
        return new MutableShorts(array, 0, array.length);
    }

    public static MutableShorts wrap(short[] array, int fromIndex, int toIndex) {
        return new MutableShorts(array, fromIndex, toIndex);
    }

    public void setShort(int index, short value) {
        Check.index(index, size());
        array[fromIndex + index] = value;
    }

    @Override
    public Short set(int index, Short element) {
        short oldValue = getShort(index);
        setShort(index, element);
        return oldValue;
    }
}
