package be.twofold.valen.format.granite.gdex;

import java.util.*;

final class GdexDouble extends Gdex {
    private final double value;

    GdexDouble(GdexItemTag tag, double value) {
        super(tag);
        this.value = value;
    }

    @Override
    public Optional<Number> asNumber() {
        return Optional.of(value);
    }

}
