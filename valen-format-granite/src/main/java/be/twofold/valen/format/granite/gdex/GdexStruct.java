package be.twofold.valen.format.granite.gdex;

import java.util.*;

final class GdexStruct extends Gdex {
    private final List<Gdex> values;

    GdexStruct(GdexItemTag tag, List<Gdex> values) {
        super(tag);
        this.values = List.copyOf(values);
    }
}
