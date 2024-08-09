package be.twofold.valen.core.compression.oodle.jna;

import be.twofold.valen.core.util.*;

public enum OodleLZ_CheckCRC implements NativeEnum {
    No(0),
    Yes(1),
    Force32(0x40000000);

    private final int nativeValue;

    OodleLZ_CheckCRC(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    @Override
    public int nativeValue() {
        return nativeValue;
    }
}
