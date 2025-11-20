package be.twofold.valen.game.gustav.reader.pak;

import java.util.*;

public enum Compression {
    METHOD_ZLIB(0x01),
    METHOD_LZ4(0x02),
    METHOD_ZSTD(0x03),
    COMPRESS_FAST(0x10),
    COMPRESS_DEFAULT(0x20),
    COMPRESS_MAX(0x40),
    ;
    private static final Compression[] VALUES = values();
    private final int value;

    Compression(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Set<Compression> fromValue(int value) {
        var result = EnumSet.noneOf(Compression.class);
        for (var compression : VALUES) {
            if ((value & compression.value) == compression.value) {
                result.add(compression);
                value &= ~compression.value;
            }
        }
        if (value != 0) {
            throw new IllegalArgumentException("Unknown compression value: " + value);
        }
        return result;
    }
}
