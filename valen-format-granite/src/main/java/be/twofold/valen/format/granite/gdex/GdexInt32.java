package be.twofold.valen.format.granite.gdex;

import java.util.*;

final class GdexInt32 extends Gdex {
    private final int value;

    GdexInt32(GdexItemTag tag, int value) {
        super(tag);
        this.value = value;
    }

    @Override
    public Optional<Number> asNumber() {
        return Optional.of(value);
    }

}
