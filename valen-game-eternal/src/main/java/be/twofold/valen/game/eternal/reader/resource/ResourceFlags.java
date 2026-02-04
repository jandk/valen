package be.twofold.valen.game.eternal.reader.resource;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public enum ResourceFlags implements ValueEnum<Integer> {
    RES_ENTRY_FLAG_NONE(0),
    RES_ENTRY_FLAG_DEFAULTED(1),
    RES_ENTRY_FLAG_HAS_VARIATION(2),
    ;

    private final int value;

    ResourceFlags(int value) {
        this.value = value;
    }

    public static ResourceFlags read(BinarySource source) throws IOException {
        return ValueEnum.fromValue(ResourceFlags.class, source.readInt());
    }

    @Override
    public Integer value() {
        return value;
    }
}
