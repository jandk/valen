package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class FioBitSetSerializer implements FioSerializer<BitSet> {
    @Override
    public BitSet load(DataSource source) throws IOException {
        int count = source.readInt();

        return BitSet.valueOf(source.readBytes(((count + 31) / 32) * 4));
    }

    @Override
    public int flags() {
        return 0;
    }
}
