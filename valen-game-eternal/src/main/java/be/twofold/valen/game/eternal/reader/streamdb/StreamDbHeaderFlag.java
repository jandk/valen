package be.twofold.valen.game.eternal.reader.streamdb;

import be.twofold.valen.core.util.*;

import java.util.*;

@SuppressWarnings("PointlessBitwiseExpression")
public enum StreamDbHeaderFlag implements FlagEnum {
    SDHF_NO_GUID(1 << 0),
    SDHF_HAS_PREFETCH_BLOCKS(1 << 1);

    private final int value;

    StreamDbHeaderFlag(int value) {
        this.value = value;
    }

    public static Set<StreamDbHeaderFlag> fromValue(int value) {
        return FlagEnum.fromValue(StreamDbHeaderFlag.class, value);
    }

    @Override
    public int value() {
        return value;
    }
}
