package be.twofold.valen.game.greatcircle.reader.image;

import be.twofold.valen.core.util.*;

public enum ImageTextureType implements ValueEnum<Integer> {
    TT_2D(0),
    TT_3D(1),
    TT_CUBIC(2);

    private final int value;

    ImageTextureType(int value) {
        this.value = value;
    }

    public static ImageTextureType fromValue(int code) {
        return ValueEnum.fromValue(ImageTextureType.class, code);
    }

    @Override
    public Integer value() {
        return value;
    }
}
