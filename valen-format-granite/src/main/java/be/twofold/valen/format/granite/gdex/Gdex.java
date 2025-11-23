package be.twofold.valen.format.granite.gdex;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.time.*;
import java.util.*;

public sealed abstract class Gdex
    permits GdexArray, GdexDate, GdexDouble, GdexFloat, GdexGuid, GdexInt32, GdexInt64, GdexRaw, GdexString, GdexStruct {

    private final GdexItemTag tag;

    Gdex(GdexItemTag tag) {
        this.tag = Objects.requireNonNull(tag);
    }

    public static Gdex read(BinaryReader reader) throws IOException {
        return new GdexReader(reader).read();
    }

    public GdexItemTag tag() {
        return tag;
    }

    public List<?> asArray() {
        throw new ClassCastException(getClass().getSimpleName());
    }

    public Bytes asBytes() {
        throw new ClassCastException(getClass().getSimpleName());
    }

    public Instant asDate() {
        throw new ClassCastException(getClass().getSimpleName());
    }

    public UUID asGuid() {
        throw new ClassCastException(getClass().getSimpleName());
    }

    public Number asNumber() {
        throw new ClassCastException(getClass().getSimpleName());
    }

    public String asString() {
        throw new ClassCastException(getClass().getSimpleName());
    }

    public GdexStruct asStruct() {
        throw new ClassCastException(getClass().getSimpleName());
    }

    @Override
    public final String toString() {
        return new Stringifier().stringify(this);
    }
}
