package be.twofold.valen.game.deathloop.index;

import be.twofold.valen.core.io.*;

import java.io.*;

public record IndexHeader(
    int magic,
    int size,
    int count
) {
    public static IndexHeader read(DataSource source) throws IOException {
        var magic = source.readIntBE();
        var size = source.readIntBE();
        for (var i = 0; i < 6; i++) {
            source.expectInt(0);
        }
        var count = source.readIntBE();
        return new IndexHeader(magic, size, count);
    }
}
