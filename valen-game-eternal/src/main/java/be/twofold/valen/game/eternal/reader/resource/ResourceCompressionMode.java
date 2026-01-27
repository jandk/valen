package be.twofold.valen.game.eternal.reader.resource;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public enum ResourceCompressionMode implements ValueEnum<Integer> {
    RES_COMP_MODE_NONE(0),
    RES_COMP_MODE_ZLIB(1),
    RES_COMP_MODE_KRAKEN(2),
    RES_COMP_MODE_LZNA(3),
    RES_COMP_MODE_KRAKEN_CHUNKED(4),
    RES_COMP_MODE_LEVIATHAN(5),
    RES_COMP_MODE_ENUM_MAX(6),
    ;

    private final int value;

    ResourceCompressionMode(int value) {
        this.value = value;
    }

    public static ResourceCompressionMode read(BinarySource source) throws IOException {
        return ValueEnum.fromValue(ResourceCompressionMode.class, Byte.toUnsignedInt(source.readByte()));
    }

    @Override
    public Integer value() {
        return value;
    }
}
