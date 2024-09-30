package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;

import java.io.*;

public class FioStringSerializer implements FioSerializer<String> {
    @Override
    public String load(DataSource source) throws IOException {
        String val = source.readPString();
        return val;
    }

    @Override
    public int flags() {
        return 9;
    }
}
