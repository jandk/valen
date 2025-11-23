package be.twofold.valen.format.granite.gdex;

import java.time.*;

final class GdexDate extends Gdex {
    private final Instant value;

    GdexDate(GdexItemTag tag, Instant value) {
        super(tag);
        this.value = value;
    }

    @Override
    public Instant asDate() {
        return value;
    }
}
