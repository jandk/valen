package be.twofold.valen.game.greatcircle.reader.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ResourcesSpecialHash(
    long unknownHash1,
    long unknownHash2
) {
    public static ResourcesSpecialHash read(BinaryReader reader) throws IOException {
        var unknownHash1 = reader.readLong();
        var unknownHash2 = reader.readLong();

        return new ResourcesSpecialHash(
            unknownHash1,
            unknownHash2
        );
    }
}
