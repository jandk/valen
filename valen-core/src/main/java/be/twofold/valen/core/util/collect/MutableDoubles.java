package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public final class MutableDoubles extends Doubles {
    private MutableDoubles(double[] array, int offset, int length) {
        super(array, offset, length);
    }

    public static MutableDoubles wrap(double[] array) {
        return new MutableDoubles(array, 0, array.length);
    }

    public static MutableDoubles wrap(double[] array, int offset, int length) {
        return new MutableDoubles(array, offset, length);
    }

    public static MutableDoubles allocate(int length) {
        return new MutableDoubles(new double[length], 0, length);
    }

    public MutableDoubles set(int index, double value) {
        Check.index(index, length);
        array[offset + index] = value;
        return this;
    }

    public MutableDoubles slice(int offset) {
        return slice(offset, length - offset);
    }

    public MutableDoubles slice(int offset, int length) {
        Check.fromIndexSize(offset, length, this.length);
        return new MutableDoubles(array, this.offset + offset, length);
    }

    public MutableDoubles fill(double value) {
        Arrays.fill(array, offset, offset + length, value);
        return this;
    }

    public DoubleBuffer asMutableBuffer() {
        return DoubleBuffer.wrap(array, offset, length);
    }
}
