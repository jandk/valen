package be.twofold.valen.format.granite.gdex;

import java.util.*;

public final class GdexStruct extends Gdex {
    private final List<Gdex> values;

    GdexStruct(GdexItemTag tag, List<Gdex> values) {
        super(tag);
        this.values = List.copyOf(values);
    }

    public List<Gdex> values() {
        return values;
    }

    @Override
    public Optional<GdexStruct> asStruct() {
        return Optional.of(this);
    }

}
