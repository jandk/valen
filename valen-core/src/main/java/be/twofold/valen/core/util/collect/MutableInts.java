package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public final class MutableInts extends Ints {
    private MutableInts(int[] array, int offset, int length) {
        super(array, offset, length);
    }

    public static MutableInts wrap(int[] array) {
        return new MutableInts(array, 0, array.length);
    }

    public static MutableInts wrap(int[] array, int offset, int length) {
        return new MutableInts(array, offset, length);
    }

    public static MutableInts allocate(int length) {
        return new MutableInts(new int[length], 0, length);
    }

    public MutableInts set(int index, int value) {
        Check.index(index, length);
        array[offset + index] = value;
        return this;
    }

    public MutableInts slice(int offset) {
        return slice(offset, length - offset);
    }

    public MutableInts slice(int offset, int length) {
        Check.fromIndexSize(offset, length, this.length);
        return new MutableInts(array, this.offset + offset, length);
    }

    public MutableInts fill(int value) {
        Arrays.fill(array, offset, offset + length, value);
        return this;
    }

    public IntBuffer asMutableBuffer() {
        return IntBuffer.wrap(array, offset, length);
    }
}
