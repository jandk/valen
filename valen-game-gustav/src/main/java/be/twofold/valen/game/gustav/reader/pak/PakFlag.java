package be.twofold.valen.game.gustav.reader.pak;

import org.slf4j.*;

import java.util.*;

public enum PakFlag {
    AllowMemoryMapping(2),
    Solid(4),
    Preload(8),
    ;

    private static final Logger log = LoggerFactory.getLogger(PakFlag.class);
    private static final PakFlag[] VALUES = values();
    private final int value;

    PakFlag(int value) {
        this.value = value;
    }

    public static Set<PakFlag> fromValue(int value) {
        var result = EnumSet.noneOf(PakFlag.class);
        for (var pakFlag : VALUES) {
            if ((value & pakFlag.value) == pakFlag.value) {
                result.add(pakFlag);
                value &= ~pakFlag.value;
            }
        }
        if (value != 0) {
            log.warn("Unknown pak flag value: {}", value);
        }
        return result;
    }
}
