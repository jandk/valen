package be.twofold.valen.game.eternal.defines;

import be.twofold.valen.core.util.*;

public enum BufferViewFlag implements ValueEnum<Integer> {
    BVF_WRITABLE(1),
    BVF_COHERENT(2),
    BVF_WRITEONLY(4),
    ;

    private final int value;

    BufferViewFlag(int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }
}
