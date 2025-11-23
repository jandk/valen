package be.twofold.valen.format.granite.gdex;

import java.util.*;

abstract sealed class GdexArray<T> extends Gdex
    permits GdexDoubleArray, GdexFloatArray, GdexGuidArray, GdexInt32Array, GdexInt64Array {

    private final List<T> values;

    GdexArray(GdexItemTag tag, List<T> values) {
        super(tag);
        this.values = List.copyOf(values);
    }

    @Override
    public Optional<List<?>> asArray() {
        return Optional.of(values);
    }

}
