package be.twofold.valen.format.granite.gdex;

import java.util.*;

final class GdexFloat extends Gdex {
    private final float value;

    GdexFloat(GdexItemTag tag, float value) {
        super(tag);
        this.value = value;
    }

    @Override
    public Optional<Number> asNumber() {
        return Optional.of(value);
    }

}
