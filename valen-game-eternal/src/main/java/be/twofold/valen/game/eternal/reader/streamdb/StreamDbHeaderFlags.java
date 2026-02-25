package be.twofold.valen.game.eternal.reader.streamdb;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;

public enum StreamDbHeaderFlags implements FlagEnum {
    SDHF_NO_GUID(0x1),
    SDHF_HAS_PREFETCH_BLOCKS(0x2),
    ;

    private final int value;

    StreamDbHeaderFlags(int value) {
        this.value = value;
    }

    public static Set<StreamDbHeaderFlags> read(BinarySource source) throws IOException {
        return FlagEnum.fromValue(StreamDbHeaderFlags.class, source.readInt());
    }

    @Override
    public int value() {
        return value;
    }
}
