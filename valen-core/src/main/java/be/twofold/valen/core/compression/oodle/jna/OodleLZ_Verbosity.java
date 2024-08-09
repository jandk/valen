package be.twofold.valen.core.compression.oodle.jna;

import be.twofold.valen.core.util.*;

public enum OodleLZ_Verbosity implements NativeEnum {
    None(0),
    Minimal(1),
    Some(2),
    Lots(3),
    Force32(0x40000000);

    private final int nativeValue;

    OodleLZ_Verbosity(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    @Override
    public int nativeValue() {
        return nativeValue;
    }
}
