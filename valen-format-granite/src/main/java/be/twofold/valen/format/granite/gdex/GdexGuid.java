package be.twofold.valen.format.granite.gdex;

import java.util.*;

final class GdexGuid extends Gdex {
    private final UUID value;

    GdexGuid(GdexItemTag tag, UUID value) {
        super(tag);
        this.value = value;
    }
}
