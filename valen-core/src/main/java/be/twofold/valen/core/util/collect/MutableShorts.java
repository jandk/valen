package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

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

    public static MutableShorts allocate(int size) {
        return new MutableShorts(new short[size], 0, size);
    }

    public MutableShorts setShort(int index, short value) {
        Check.index(index, size());
        array[fromIndex + index] = value;
        return this;
    }

    public MutableShorts fill(short value) {
        Arrays.fill(array, fromIndex, toIndex, value);
        return this;
    }

    public ShortBuffer asMutableBuffer() {
        return ShortBuffer.wrap(array, fromIndex, size());
    }

    public MutableShorts slice(int fromIndex) {
        return slice(fromIndex, size());
    }

    public MutableShorts slice(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, size());
        return new MutableShorts(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }
}
