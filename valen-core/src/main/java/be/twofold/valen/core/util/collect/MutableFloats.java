package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public final class MutableFloats extends Floats {
    private MutableFloats(float[] array, int offset, int length) {
        super(array, offset, length);
    }

    public static MutableFloats wrap(float[] array) {
        return new MutableFloats(array, 0, array.length);
    }

    public static MutableFloats wrap(float[] array, int offset, int length) {
        return new MutableFloats(array, offset, length);
    }

    public static MutableFloats allocate(int length) {
        return new MutableFloats(new float[length], 0, length);
    }

    public MutableFloats set(int index, float value) {
        Check.index(index, length);
        array[offset + index] = value;
        return this;
    }

    public MutableFloats fill(float value) {
        Arrays.fill(array, offset, offset + length, value);
        return this;
    }

    public FloatBuffer asMutableBuffer() {
        return FloatBuffer.wrap(array, offset, length);
    }

    public MutableFloats slice(int offset) {
        return slice(offset, length - offset);
    }

    public MutableFloats slice(int offset, int length) {
        Check.fromIndexSize(offset, length, this.length);
        return new MutableFloats(array, this.offset + offset, length);
    }
}
