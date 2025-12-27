package be.twofold.valen.format.granite.gdex;

import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public enum GdexItemFlags implements ValueEnum<Integer> {
    NONE(0),
    LONG_HEADER(1),
    ;

    private final int value;

    GdexItemFlags(int value) {
        this.value = value;
    }

    public static GdexItemFlags read(BinarySource source) throws IOException {
        return ValueEnum.fromValue(GdexItemFlags.class, (int) source.readByte());
    }

    @Override
    public Integer value() {
        return value;
    }
}
