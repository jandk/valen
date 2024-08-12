package be.twofold.valen.game.eternal.reader.resource;

import be.twofold.valen.core.util.*;

import java.util.*;

public enum ResourceCompressionMode implements ValueEnum<Integer> {
    RES_COMP_MODE_NONE(0),
    RES_COMP_MODE_ZLIB(1),
    RES_COMP_MODE_KRAKEN(2),
    RES_COMP_MODE_LZNA(3),
    RES_COMP_MODE_KRAKEN_CHUNKED(4),
    RES_COMP_MODE_LEVIATHAN(5),
    RES_COMP_MODE_ENUM_MAX(6);

    private static final Map<Integer, ResourceCompressionMode> values = ValueEnum.valueMap(ResourceCompressionMode.class);
    private final int value;

    ResourceCompressionMode(int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }

    public static ResourceCompressionMode fromValue(Integer value) {
        return ValueEnum.fromValue(values, value)
            .orElseThrow(() -> new IllegalArgumentException("Unknown resource compression mode: " + value));
    }
}
