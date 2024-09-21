package be.twofold.valen.game.deathloop.image;

import java.util.*;

public enum ImageTextureFormat {
    FMT_R8(1),
    FMT_R16F(2),
    FMT_R32F(3),
    FMT_R16_UNORM(4),
    FMT_RG32F(6),
    FMT_RGBA(10),
    FMT_RGBA16F(11),
    FMT_RGBA32F(12),
    FMT_BC1(14),
    FMT_BC3(16),
    FMT_BC4(17),
    FMT_BC5S(18),
    FMT_BC6H(19),
    FMT_BC7(20);
    private static final ImageTextureFormat[] VALUES = values();
    private final int code;

    ImageTextureFormat(int code) {
        this.code = code;
    }

    public static ImageTextureFormat fromCode(int code) {
        return Arrays.stream(VALUES)
            .filter(value -> value.code == code)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown texture format: " + code));
    }
}
