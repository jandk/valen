package be.twofold.valen.game.colossus.reader.resources;

import be.twofold.valen.core.util.*;

public enum ResourceCompressionMode implements ValueEnum<Integer> {
    RES_COMP_MODE_NONE(0),
    RES_COMP_MODE_ZLIB(1),
    RES_COMP_MODE_KRAKEN(2),
    RES_COMP_MODE_LZNA(3),
    RES_COMP_MODE_KRAKEN_CHUNKED(4),
    RES_COMP_MODE_ENUM_MAX(5),
    ;

    private final int value;

    ResourceCompressionMode(int value) {
        this.value = value;
    }

    public static ResourceCompressionMode fromValue(int value) {
        return ValueEnum.fromValue(ResourceCompressionMode.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
