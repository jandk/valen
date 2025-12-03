package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public final class MutableShorts extends Shorts {
    private MutableShorts(short[] array, int offset, int length) {
        super(array, offset, length);
    }

    public static MutableShorts wrap(short[] array) {
        return new MutableShorts(array, 0, array.length);
    }

    public static MutableShorts wrap(short[] array, int offset, int length) {
        return new MutableShorts(array, offset, length);
    }

    public static MutableShorts allocate(int length) {
        return new MutableShorts(new short[length], 0, length);
    }

    public MutableShorts set(int index, short value) {
        Check.index(index, length);
        array[offset + index] = value;
        return this;
    }

    public MutableShorts fill(short value) {
        Arrays.fill(array, offset, offset + length, value);
        return this;
    }

    public ShortBuffer asMutableBuffer() {
        return ShortBuffer.wrap(array, offset, length);
    }

    public MutableShorts slice(int offset) {
        return slice(offset, length - offset);
    }

    public MutableShorts slice(int offset, int length) {
        Check.fromIndexSize(offset, length, this.length);
        return new MutableShorts(array, this.offset + offset, length);
    }
}
