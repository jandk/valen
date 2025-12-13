package be.twofold.valen.game.gustav.reader.pak;

import be.twofold.valen.core.util.*;

import java.util.*;

public enum Compression implements FlagEnum {
    METHOD_ZLIB(0x01),
    METHOD_LZ4(0x02),
    METHOD_ZSTD(0x03),
    COMPRESS_FAST(0x10),
    COMPRESS_DEFAULT(0x20),
    COMPRESS_MAX(0x40),
    ;
    private final int value;

    Compression(int value) {
        this.value = value;
    }

    public static Set<Compression> fromValue(int value) {
        return FlagEnum.fromValue(Compression.class, value);
    }

    @Override
    public int value() {
        return value;
    }
}
