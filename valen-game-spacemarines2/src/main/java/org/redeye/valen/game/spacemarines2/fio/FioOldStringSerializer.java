package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;

public class FioOldStringSerializer implements FioSerializer<String> {
    @Override
    public String load(DataSource source) throws IOException {
        return source.readString(Short.toUnsignedInt(source.readShort()));
    }

    @Override
    public int flags() {
        return 0;
    }
}
