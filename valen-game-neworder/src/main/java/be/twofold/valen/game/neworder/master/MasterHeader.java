package be.twofold.valen.game.neworder.master;

import be.twofold.valen.core.io.*;

import java.io.*;

public record MasterHeader(int magic, int count) {
    public static MasterHeader read(DataSource source) throws IOException {
        var magic = source.readIntBE();
        var count = source.readIntBE();
        return new MasterHeader(magic, count);
    }
}
