package be.twofold.valen.game.darkages.reader.resources;

import wtf.reversed.toolbox.util.*;

public enum ResourcesCompressionMode implements ValueEnum<Integer> {
    RES_COMP_MODE_NONE(0),
    RES_COMP_MODE_ZLIB(1),
    RES_COMP_MODE_KRAKEN(2),
    RES_COMP_MODE_LZNA(3),
    RES_COMP_MODE_KRAKEN_CHUNKED(4),
    RES_COMP_MODE_LEVIATHAN(5),
    RES_COMP_MODE_BCPACK(6),
    RES_COMP_MODE_ENUM_MAX(7),
    ;

    private final int value;

    ResourcesCompressionMode(int value) {
        this.value = value;
    }

    public static ResourcesCompressionMode fromValue(Integer value) {
        return ValueEnum.fromValue(ResourcesCompressionMode.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
    }
