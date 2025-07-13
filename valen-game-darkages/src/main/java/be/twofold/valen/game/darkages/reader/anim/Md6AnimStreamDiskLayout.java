package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;

public record Md6AnimStreamDiskLayout(
    int uncompressedSize,
    int compressedSize,
    int offset,
    short numFramesets,
    short compression
) {
    public static Md6AnimStreamDiskLayout read(BinaryReader reader) throws IOException {
        var uncompressedSize = reader.readInt();
        var compressedSize = reader.readInt();
        var offset = reader.readInt();
        var numFramesets = reader.readShort();
        var compression = reader.readShort();

        return new Md6AnimStreamDiskLayout(
            uncompressedSize,
            compressedSize,
            offset,
            numFramesets,
            compression
        );
    }
}
