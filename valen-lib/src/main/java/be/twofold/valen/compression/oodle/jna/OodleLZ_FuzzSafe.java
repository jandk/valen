package be.twofold.valen.compression.oodle.jna;

import be.twofold.valen.util.*;

public enum OodleLZ_FuzzSafe implements NativeEnum {
    No(0),
    Yes(1);

    private final int nativeValue;

    OodleLZ_FuzzSafe(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    @Override
    public int nativeValue() {
        return nativeValue;
    }
}
