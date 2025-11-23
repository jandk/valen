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

    public Optional<List<?>> asArray() {
        return Optional.empty();
    }

    public Optional<Bytes> asBytes() {
        return Optional.empty();
    }

    public Optional<Instant> asDate() {
        return Optional.empty();
    }

    public Optional<UUID> asGuid() {
        return Optional.empty();
    }

    public Optional<Number> asNumber() {
        return Optional.empty();
    }

    public Optional<String> asString() {
        return Optional.empty();
    }

    public Optional<GdexStruct> asStruct() {
        return Optional.empty();
    }

    @Override
    public final String toString() {
        return new Stringifier().stringify(this);
    }
}
