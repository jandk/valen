package be.twofold.valen.game.eternal.defines;

import be.twofold.valen.core.util.*;

public enum TextureFilter implements ValueEnum<Integer> {
    TF_LINEAR(0),
    TF_NEAREST(1),
    TF_MIN(2),
    TF_MAX(3),
    TF_NEAREST_MIPMAP_NEAREST(4),
    TF_LINEAR_MIPMAP_NEAREST(5),
    TF_TRILINEAR(6),
    TF_DEFAULT(7),
    ;

    private final int value;

    TextureFilter(int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }
}
