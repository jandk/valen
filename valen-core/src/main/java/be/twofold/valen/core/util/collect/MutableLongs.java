package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public final class MutableLongs extends Longs {
    private MutableLongs(long[] array, int offset, int length) {
        super(array, offset, length);
    }

    public static MutableLongs wrap(long[] array) {
        return new MutableLongs(array, 0, array.length);
    }

    public static MutableLongs wrap(long[] array, int offset, int length) {
        return new MutableLongs(array, offset, length);
    }

    public static MutableLongs allocate(int length) {
        return new MutableLongs(new long[length], 0, length);
    }

    public MutableLongs set(int index, long value) {
        Check.index(index, length);
        array[offset + index] = value;
        return this;
    }

    public MutableLongs fill(long value) {
        Arrays.fill(array, offset, offset + length, value);
        return this;
    }

    public LongBuffer asMutableBuffer() {
        return LongBuffer.wrap(array, offset, length);
    }

    public MutableLongs slice(int offset) {
        return slice(offset, length - offset);
    }

    public MutableLongs slice(int offset, int length) {
        Check.fromIndexSize(offset, length, this.length);
        return new MutableLongs(array, this.offset + offset, length);
    }
}
