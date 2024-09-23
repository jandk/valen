package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class FioBitSetFlagsSerializer implements FioSerializer<BitSet> {
    private int flags;

    public FioBitSetFlagsSerializer() {
        this.flags = 0;
    }

    @Override
    public BitSet load(DataSource source) throws IOException {
        var count = source.readShort();
        var roundedCount = (count + 7) / 8;
        return BitSet.valueOf(source.readBytes(roundedCount));
    }

    @Override
    public int flags() {
        return flags;
    }

}
