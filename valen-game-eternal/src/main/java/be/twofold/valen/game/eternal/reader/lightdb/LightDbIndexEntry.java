package be.twofold.valen.game.eternal.reader.lightdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record LightDbIndexEntry(
    short imageIndex,
    short x,
    short y,
    short width,
    short height
) {
    public static LightDbIndexEntry read(DataSource source) throws IOException {
        var imageIndex = source.readShort();
        var x = source.readShort();
        var y = source.readShort();
        var width = source.readShort();
        var height = source.readShort();

        return new LightDbIndexEntry(
            imageIndex,
            x,
            y,
            width,
            height
        );
    }
}
