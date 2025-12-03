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

    public static MutableShorts allocate(int length) {
        return new MutableShorts(new short[length], 0, length);
    }

    public MutableShorts set(int index, short value) {
        Check.index(index, length());
        array[fromIndex + index] = value;
        return this;
    }

    public MutableShorts fill(short value) {
        Arrays.fill(array, fromIndex, toIndex, value);
        return this;
    }

    public ShortBuffer asMutableBuffer() {
        return ShortBuffer.wrap(array, fromIndex, length());
    }

    public MutableShorts slice(int fromIndex) {
        return slice(fromIndex, length());
    }

    public MutableShorts slice(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, length());
        return new MutableShorts(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }
}
