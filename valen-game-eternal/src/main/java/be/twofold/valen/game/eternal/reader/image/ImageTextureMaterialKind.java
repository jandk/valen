package be.twofold.valen.game.eternal.reader.image;

import java.util.*;

public enum ImageTextureMaterialKind {
    TMK_NONE(0),
    TMK_ALBEDO(1),
    TMK_SPECULAR(2),
    TMK_NORMAL(3),
    TMK_SMOOTHNESS(4),
    TMK_COVER(5),
    TMK_SSSMASK(6),
    TMK_COLORMASK(7),
    TMK_BLOOMMASK(8),
    TMK_HEIGHTMAP(9),
    TMK_DECALALBEDO(10),
    TMK_DECALNORMAL(11),
    TMK_DECALSPECULAR(12),
    TMK_LIGHTPROJECT(13),
    TMK_PARTICLE(14),
    TMK_UNUSED_1(15),
    TMK_UNUSED_2(16),
    TMK_LIGHTMAP(17),
    TMK_UI(18),
    TMK_FONT(19),
    TMK_LEGACY_FLASH_UI(20),
    TMK_LIGHTMAP_DIRECTIONAL(21),
    TMK_BLENDMASK(22);

    private static final ImageTextureMaterialKind[] VALUES = values();
    private final int code;

    ImageTextureMaterialKind(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ImageTextureMaterialKind fromCode(int code) {
        return Arrays.stream(VALUES)
            .filter(value -> value.code == code)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown texture material kind: " + code));
    }
}
