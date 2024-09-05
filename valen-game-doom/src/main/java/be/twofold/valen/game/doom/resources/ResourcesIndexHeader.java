package be.twofold.valen.game.doom.resources;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ResourcesIndexHeader(
    int magic,
    int size
) {
    public static ResourcesIndexHeader read(DataSource source) throws IOException {
        int magic = source.readInt();
        int size = source.readIntBE();
        source.expectLong(0);
        source.expectLong(0);
        source.expectLong(0);
        return new ResourcesIndexHeader(magic, size);
    }

    public int version() {
        return magic & 0xff;
    }
}
