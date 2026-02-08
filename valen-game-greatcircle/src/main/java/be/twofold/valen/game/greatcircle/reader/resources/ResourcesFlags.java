package be.twofold.valen.game.greatcircle.reader.resources;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;

public enum ResourcesFlags implements FlagEnum {
    RES_ENTRY_FLAG_DEFAULTED(0x1),
    RES_ENTRY_FLAG_HAS_VARIATION(0x2),
    RES_ENTRY_FLAG_FARMHASH(0x4),
    RES_ENTRY_FLAG_EMBEDDED(0x8),
    RES_ENTRY_FLAG_META(0x10),
    RES_ENTRY_FLAG_STREAM_FILE(0x20),
    ;

    private final int value;

    ResourcesFlags(int value) {
        this.value = value;
    }

    public static Set<ResourcesFlags> read(BinarySource source) throws IOException {
        return FlagEnum.fromValue(ResourcesFlags.class, source.readInt());
    }

    @Override
    public int value() {
        return value;
    }
}
