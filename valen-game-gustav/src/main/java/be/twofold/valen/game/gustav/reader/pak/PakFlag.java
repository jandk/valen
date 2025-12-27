package be.twofold.valen.game.gustav.reader.pak;

import wtf.reversed.toolbox.util.*;

import java.util.*;

public enum PakFlag implements FlagEnum {
    AllowMemoryMapping(2),
    Solid(4),
    Preload(8),
    ;
    private final int value;

    PakFlag(int value) {
        this.value = value;
    }

    public static Set<PakFlag> fromValue(int value) {
        return FlagEnum.fromValue(PakFlag.class, value);
    }

    @Override
    public int value() {
        return value;
    }
}
