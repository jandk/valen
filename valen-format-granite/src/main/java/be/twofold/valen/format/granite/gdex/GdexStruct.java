package be.twofold.valen.format.granite.gdex;

import java.util.*;
import java.util.stream.*;

public final class GdexStruct extends Gdex {
    private final List<Gdex> values;

    GdexStruct(GdexItemTag tag, List<Gdex> values) {
        super(tag);
        this.values = List.copyOf(values);
    }

    public List<Gdex> values() {
        return values;
    }

    public Stream<Gdex> find(GdexItemTag tag) {
        return values.stream()
            .filter(gdex -> gdex.tag() == tag);
    }

    public Gdex findOne(GdexItemTag tag) {
        var iterator = find(tag).iterator();
        var result = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalStateException("Expected one of " + tag);
        }
        return result;
    }

    @Override
    public GdexStruct asStruct() {
        return this;
    }
}
