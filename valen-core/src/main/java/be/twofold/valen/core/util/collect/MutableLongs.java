package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

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

    public static MutableLongs allocate(int length) {
        return new MutableLongs(new long[length], 0, length);
    }

    public MutableLongs set(int index, long value) {
        Check.index(index, length());
        array[fromIndex + index] = value;
        return this;
    }

    public MutableLongs fill(long value) {
        Arrays.fill(array, fromIndex, toIndex, value);
        return this;
    }

    public LongBuffer asMutableBuffer() {
        return LongBuffer.wrap(array, fromIndex, length());
    }

    public MutableLongs slice(int fromIndex) {
        return slice(fromIndex, length());
    }

    public MutableLongs slice(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, length());
        return new MutableLongs(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }
}
