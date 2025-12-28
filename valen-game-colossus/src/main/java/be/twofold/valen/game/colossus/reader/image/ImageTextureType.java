package be.twofold.valen.game.colossus.reader.image;

import be.twofold.valen.core.util.*;

public enum ImageTextureType implements ValueEnum<Integer> {
    TT_2D(0x00),
    TT_3D(0x01),
    TT_CUBIC(0x02);

    private final int value;

    ImageTextureType(int value) {
        this.value = value;
    }

    public static ImageTextureType fromCode(int value) {
        return ValueEnum.fromValue(ImageTextureType.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
