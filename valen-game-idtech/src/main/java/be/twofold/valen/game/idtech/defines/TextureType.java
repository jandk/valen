package be.twofold.valen.game.idtech.defines;

import be.twofold.valen.core.util.*;

public enum TextureType implements ValueEnum<Integer> {
    TT_2D(0),
    TT_3D(1),
    TT_CUBIC(2),
    ;

    private final int value;

    TextureType(int value) {
        this.value = value;
    }

    public static TextureType fromValue(int value) {
        return ValueEnum.fromValue(TextureType.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
