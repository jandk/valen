package be.twofold.valen.game.eternal.reader.image;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

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
    TMK_LIGHTMAP(17),
    TMK_UI(18),
    TMK_FONT(19),
    TMK_LEGACY_FLASH_UI(20),
    TMK_LIGHTMAP_DIRECTIONAL(21),
    TMK_BLENDMASK(22),
    TMK_COUNT(23),
    ;

    private final int value;

    TextureMaterialKind(int value) {
        this.value = value;
    }

    public static TextureMaterialKind read(BinarySource source) throws IOException {
        return ValueEnum.fromValue(TextureMaterialKind.class, source.readInt());
    }

    @Override
    public Integer value() {
        return value;
    }
}
