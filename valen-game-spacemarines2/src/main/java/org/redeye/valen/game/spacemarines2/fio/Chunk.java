package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Chunk(
    short id,
    int endOffset
) {
    public static Chunk read(DataSource source) throws IOException {
        return new Chunk(source.readShort(), source.readInt());
    }

    public boolean isTerminator() {
        return id == -1;
    }
}
