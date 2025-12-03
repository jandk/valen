package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public final class MutableDoubles extends Doubles {
    private MutableDoubles(double[] array, int fromIndex, int toIndex) {
        super(array, fromIndex, toIndex);
    }

    public static MutableDoubles wrap(double[] array) {
        return new MutableDoubles(array, 0, array.length);
    }

    public static MutableDoubles wrap(double[] array, int fromIndex, int toIndex) {
        return new MutableDoubles(array, fromIndex, toIndex);
    }

    public static MutableDoubles allocate(int length) {
        return new MutableDoubles(new double[length], 0, length);
    }

    public MutableDoubles setDouble(int index, double value) {
        Check.index(index, length());
        array[fromIndex + index] = value;
        return this;
    }

    public MutableDoubles fill(double value) {
        Arrays.fill(array, fromIndex, toIndex, value);
        return this;
    }

    public DoubleBuffer asMutableBuffer() {
        return DoubleBuffer.wrap(array, fromIndex, length());
    }

    public MutableDoubles slice(int fromIndex) {
        return slice(fromIndex, length());
    }

    public MutableDoubles slice(int fromIndex, int toIndex) {
        Check.fromToIndex(fromIndex, toIndex, length());
        return new MutableDoubles(array, this.fromIndex + fromIndex, this.fromIndex + toIndex);
    }
}
