package be.twofold.valen.game.eternal.defines;

import be.twofold.valen.core.util.*;

public enum ParmScope implements ValueEnum<Integer> {
    PSCP_VIEW(0),
    PSCP_INSTANCE(1),
    PSCP_SURFACE(2),
    PSCP_NUM(3),
    ;

    private final int value;

    ParmScope(int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }
}
