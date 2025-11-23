package be.twofold.valen.format.granite.gdex;

import java.util.*;

final class GdexString extends Gdex {
    private final String value;

    GdexString(GdexItemTag tag, String value) {
        super(tag);
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public String asString() {
        return value;
    }
}
