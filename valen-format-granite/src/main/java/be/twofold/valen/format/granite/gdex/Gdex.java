package be.twofold.valen.format.granite.gdex;

import be.twofold.valen.core.io.*;

import java.io.*;
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
}
