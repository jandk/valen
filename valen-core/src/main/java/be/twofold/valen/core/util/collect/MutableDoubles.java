package be.twofold.valen.core.util.collect;

import be.twofold.valen.core.util.*;

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

    public void setDouble(int index, double value) {
        Check.index(index, size());
        array[fromIndex + index] = value;
    }

    @Override
    public Double set(int index, Double element) {
        double oldValue = getDouble(index);
        setDouble(index, element);
        return oldValue;
    }
}
