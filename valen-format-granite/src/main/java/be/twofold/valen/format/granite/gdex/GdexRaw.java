package be.twofold.valen.format.granite.gdex;

import be.twofold.valen.core.util.collect.*;

import java.util.*;

final class GdexRaw extends Gdex {
    private final Bytes value;

    GdexRaw(GdexItemTag tag, Bytes value) {
        super(tag);
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public Bytes asBytes() {
        return value;
    }
}
