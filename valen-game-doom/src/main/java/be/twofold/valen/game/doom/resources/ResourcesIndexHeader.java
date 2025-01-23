package be.twofold.valen.game.doom.resources;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ResourcesIndexHeader(
    int magic,
    int size,
    int count
) {
    public static ResourcesIndexHeader read(DataSource source) throws IOException {
        var magic = source.readIntBE();
        var size = source.readIntBE();
        for (var i = 0; i < 6; i++) {
            source.expectInt(0);
        }
        var count = source.readIntBE();

        return new ResourcesIndexHeader(
            magic,
            size,
            count
        );
    }

    public int version() {
        return magic >>> 24;
    }
}
