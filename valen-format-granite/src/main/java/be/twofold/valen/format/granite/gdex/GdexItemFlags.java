package be.twofold.valen.format.granite.gdex;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;

public enum GdexItemFlags implements ValueEnum<Integer> {
    NONE(0),
    LONG_HEADER(1);;

    private final int value;

    GdexItemFlags(int value) {
        this.value = value;
    }

    public static GdexItemFlags read(BinaryReader reader) throws IOException {
        return ValueEnum.fromValue(GdexItemFlags.class, (int) reader.readByte());
    }

    @Override
    public Integer value() {
        return value;
    }
}
