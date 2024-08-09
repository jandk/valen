package be.twofold.valen.core.compression.oodle.jna;

import be.twofold.valen.core.util.*;

public enum OodleLZ_Decode_ThreadPhase implements NativeEnum {
    Phase1(1),
    Phase2(2),
    All(3);

    private final int nativeValue;

    OodleLZ_Decode_ThreadPhase(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    @Override
    public int nativeValue() {
        return nativeValue;
    }
}
