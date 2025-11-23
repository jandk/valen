package be.twofold.valen.format.granite.gdex;

import java.util.*;

final class GdexInt64 extends Gdex {
    private final long value;

    GdexInt64(GdexItemTag tag, long value) {
        super(tag);
        this.value = value;
    }

    @Override
    public Optional<Number> asNumber() {
        return Optional.of(value);
    }

}
