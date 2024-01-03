package be.twofold.valen.reader.streamdb;

import java.util.*;

@SuppressWarnings("PointlessBitwiseExpression")
public enum StreamDbHeaderFlag {
    SDHF_NO_GUID(1 << 0),
    SDHF_HAS_PREFETCH_BLOCKS(1 << 1);

    private final int value;

    StreamDbHeaderFlag(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static EnumSet<StreamDbHeaderFlag> fromValue(int value) {
        EnumSet<StreamDbHeaderFlag> flags = EnumSet.noneOf(StreamDbHeaderFlag.class);
        for (StreamDbHeaderFlag flag : values()) {
            if ((value & flag.getValue()) != 0) {
                flags.add(flag);
            }
        }
        return flags;
    }
}
