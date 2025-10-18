package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;

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

    public static MutableFloats allocate(int size) {
        return new MutableFloats(new float[size], 0, size);
    }

    public void setFloat(int index, float value) {
        Check.index(index, size());
        array[fromIndex + index] = value;
    }

    public FloatBuffer asMutableBuffer() {
        return FloatBuffer.wrap(array, fromIndex, size());
    }
}
