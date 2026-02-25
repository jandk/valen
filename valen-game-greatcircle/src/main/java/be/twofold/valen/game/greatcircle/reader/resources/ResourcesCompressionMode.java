package be.twofold.valen.game.greatcircle.reader.resources;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public enum ResourcesCompressionMode implements ValueEnum<Integer> {
    RES_COMP_MODE_NONE(0),
    RES_COMP_MODE_ZLIB(1),
    RES_COMP_MODE_KRAKEN(2),
    RES_COMP_MODE_LZNA(3),
    RES_COMP_MODE_KRAKEN_CHUNKED(4),
    RES_COMP_MODE_LEVIATHAN(5),
    RES_COMP_MODE_ENUM_MAX(6),
    ;

    private final int value;

    ResourcesCompressionMode(int value) {
        this.value = value;
    }

    public static ResourcesCompressionMode read(BinarySource source) throws IOException {
        return ValueEnum.fromValue(ResourcesCompressionMode.class, Byte.toUnsignedInt(source.readByte()));
    }

    @Override
    public Integer value() {
        return value;
    }
}
