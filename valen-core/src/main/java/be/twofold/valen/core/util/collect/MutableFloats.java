package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public final class MutableFloats extends Floats {
    private MutableFloats(float[] array, int fromIndex, int toIndex) {
        super(array, fromIndex, toIndex);
    }

    public static MutableFloats wrap(float[] array) {
        return new MutableFloats(array, 0, array.length);
    }

    public static MutableFloats wrap(float[] array, int fromIndex, int toIndex) {
        return new MutableFloats(array, fromIndex, toIndex);
    }

    public static MutableFloats allocate(int length) {
        return new MutableFloats(new float[length], 0, length);
    }

    public MutableFloats set(int index, float value) {
        Check.index(index, length());
        array[fromIndex + index] = value;
        return this;
    }

    public MutableFloats fill(float value) {
        Arrays.fill(array, fromIndex, toIndex, value);
        return this;
    }

    public FloatBuffer asMutableBuffer() {
        return FloatBuffer.wrap(array, fromIndex, length());
    }

    public MutableFloats slice(int fromIndex) {
        return slice(fromIndex, length());
    }

    public MutableFloats slice(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, length());
        return new MutableFloats(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }
}
