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
    public static LightDbIndexEntry read(BinaryReader reader) throws IOException {
        var imageIndex = reader.readShort();
        var x = reader.readShort();
        var y = reader.readShort();
        var width = reader.readShort();
        var height = reader.readShort();

        return new LightDbIndexEntry(
            imageIndex,
            x,
            y,
            width,
            height
        );
    }
}
