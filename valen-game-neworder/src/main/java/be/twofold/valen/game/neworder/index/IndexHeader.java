package be.twofold.valen.game.neworder.index;

import be.twofold.valen.core.io.*;

import java.io.*;

public record IndexHeader(
    int magic,
    int size,
    int mask,
    int count
) {
    public static IndexHeader read(DataSource source) throws IOException {
        var magic = source.readIntBE();
        var size = source.readIntBE();
        for (int i = 0; i < 6; i++) {
            source.expectInt(0);
        }
        var mask = source.readIntBE();
        var count = source.readIntBE();
        return new IndexHeader(magic, size, mask, count);
    }
}
