package be.twofold.valen.game.greatcircle.reader.resources;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ResourceSpecialHash(
    long unknownHash1,
    long unknownHash2
) {
    public static ResourceSpecialHash read(BinaryReader reader) throws IOException {
        long unknownHash1 = reader.readLong();
        long unknownHash2 = reader.readLong();

        return new ResourceSpecialHash(
            unknownHash1,
            unknownHash2
        );
    }
}
