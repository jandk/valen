package be.twofold.valen.game.neworder.index;

import be.twofold.valen.core.io.*;

import java.io.*;

public record IndexHeader(
    int magic,
    int size,
    int[] reserved,
    int mask,
    int count
) {
    public static IndexHeader read(DataSource source) throws IOException {
        var magic = source.readIntBE();
        var size = source.readIntBE();
        var reserved = source.readInts(6);
        var mask = source.readIntBE();
        var count = source.readIntBE();
        return new IndexHeader(magic, size, reserved, mask, count);
    }
}
