package be.twofold.valen.game.colossus.reader.image;

import be.twofold.valen.core.util.*;

public enum ImageTextureMaterialKind implements ValueEnum<Integer> {
    TMK_NONE(0x00),
    TMK_ALBEDO(0x01),
    TMK_SPECULAR(0x02),
    TMK_NORMAL(0x03),
    TMK_SMOOTHNESS(0x04),
    TMK_COVER(0x05),
    TMK_SSSMASK(0x06),
    TMK_COLORMASK(0x07),
    TMK_BLOOMMASK(0x08),
    TMK_HEIGHTMAP(0x09),
    TMK_DECALALBEDO(0x0a),
    TMK_DECALNORMAL(0x0b),
    TMK_DECALSPECULAR(0x0c),
    TMK_LIGHTPROJECT(0x0d),
    TMK_PARTICLE(0x0e),
    TMK_UNUSED_1(0x0f),
    TMK_UNUSED_2(0x10),
    TMK_LIGHTMAP(0x11),
    TMK_UI(0x12),
    TMK_FONT(0x13),
    TMK_LEGACY_FLASH_UI(0x14),
    TMK_LIGHTMAP_DIRECTIONAL(0x15),
    TMK_BLENDMASK(0x16);

    private final int value;

    ImageTextureMaterialKind(int value) {
        this.value = value;
    }

    public static ImageTextureMaterialKind fromCode(int value) {
        return ValueEnum.fromValue(ImageTextureMaterialKind.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
