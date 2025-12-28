package be.twofold.valen.game.greatcircle.reader.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ResourceSpecialHash(
    long unknownHash1,
    long unknownHash2
) {
    public static ResourceSpecialHash read(BinarySource source) throws IOException {
        long unknownHash1 = source.readLong();
        long unknownHash2 = source.readLong();

        return new ResourceSpecialHash(
            unknownHash1,
            unknownHash2
        );
    }
}
