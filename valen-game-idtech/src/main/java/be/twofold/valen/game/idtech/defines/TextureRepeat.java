package be.twofold.valen.game.idtech.defines;

import be.twofold.valen.core.util.*;

public enum TextureRepeat implements ValueEnum<Integer> {
    TR_REPEAT(0),
    TR_CLAMP(1),
    TR_CLAMP_S(2),
    TR_CLAMP_T(3),
    TR_CLAMP_TO_BORDER(4),
    TR_MIRROR(5),
    ;

    private final int value;

    TextureRepeat(int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }
}
