package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Md6AnimStreamDiskLayout(
    int uncompressedSize,
    int compressedSize,
    int offset,
    short numFramesets,
    short compression
) {
    public static Md6AnimStreamDiskLayout read(BinarySource source) throws IOException {
        var uncompressedSize = source.readInt();
        var compressedSize = source.readInt();
        var offset = source.readInt();
        var numFramesets = source.readShort();
        var compression = source.readShort();

        return new Md6AnimStreamDiskLayout(
            uncompressedSize,
            compressedSize,
            offset,
            numFramesets,
            compression
        );
    }
}
