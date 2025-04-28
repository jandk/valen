package be.twofold.valen.game.greatcircle.defines;

import be.twofold.valen.core.util.*;

public enum TextureMaterialKind implements ValueEnum<Integer> {
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
    TMK_UNUSED_3(17),
    TMK_UI(18),
    TMK_FONT(19),
    TMK_LEGACY_FLASH_UI(20),
    TMK_UNUSED_4(21),
    TMK_BLENDMASK(22),
    TMK_TINTMASK(23),
    TMK_TERRAIN_SPLATMAP(24),
    TMK_ECOTOPE_LAYER(25),
    TMK_DECALHEIGHTMAP(26),
    TMK_ALBEDO_UNSCALED(27),
    TMK_ALBEDO_DETAILS(28),
    TMK_COUNT(29),
    ;

    private final int value;

    TextureMaterialKind(int value) {
        this.value = value;
    }

    public static TextureMaterialKind fromValue(int value) {
        return ValueEnum.fromValue(TextureMaterialKind.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
