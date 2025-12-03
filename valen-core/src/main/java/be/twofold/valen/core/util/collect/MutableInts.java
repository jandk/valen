package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

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

    public static MutableInts allocate(int length) {
        return new MutableInts(new int[length], 0, length);
    }

    public MutableInts set(int index, int value) {
        Check.index(index, length());
        array[fromIndex + index] = value;
        return this;
    }

    public MutableInts fill(int value) {
        Arrays.fill(array, fromIndex, toIndex, value);
        return this;
    }

    public IntBuffer asMutableBuffer() {
        return IntBuffer.wrap(array, fromIndex, length());
    }

    public MutableInts slice(int fromIndex) {
        return slice(fromIndex, length());
    }

    public MutableInts slice(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, length());
        return new MutableInts(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }
}
