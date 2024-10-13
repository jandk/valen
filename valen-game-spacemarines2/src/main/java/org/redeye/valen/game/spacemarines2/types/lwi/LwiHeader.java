package org.redeye.valen.game.spacemarines2.types.lwi;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record LwiHeader(
    BitSet flags,
    int version,
    String containerType
) {

    public static LwiHeader read(DataSource source) throws IOException {
        return new LwiHeader(BitSet.valueOf(source.readBytes(4)), source.readInt(), source.readPString());
    }
}
