package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;

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

    public static MutableInts allocate(int size) {
        return new MutableInts(new int[size], 0, size);
    }

    public MutableInts setInt(int index, int value) {
        Check.index(index, size());
        array[fromIndex + index] = value;
        return this;
    }

    public IntBuffer asMutableBuffer() {
        return IntBuffer.wrap(array, fromIndex, size());
    }
}
