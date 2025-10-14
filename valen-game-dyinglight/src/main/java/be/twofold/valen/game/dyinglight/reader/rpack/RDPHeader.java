package be.twofold.valen.game.dyinglight.reader.rpack;

import be.twofold.valen.core.io.*;

import java.io.*;

public record RDPHeader(
    int numParts,
    int numSections,
    int numFiles,
    int numFilenameBytes,
    int numFilenameOffsets,
    int blockSize
) {
    public static RDPHeader read(BinaryReader reader) throws IOException {
        reader.expectInt(0x4C365052); // magic
        reader.expectInt(4); // version
        reader.expectInt(4096); // block alignment?

        var numParts = reader.readInt();
        var numSections = reader.readInt();
        var numFiles = reader.readInt();
        var numFilenameBytes = reader.readInt();
        var numFilenameOffsets = reader.readInt();
        var blockSize = reader.readInt();

        return new RDPHeader(
            numParts,
            numSections,
            numFiles,
            numFilenameBytes,
            numFilenameOffsets,
            blockSize
        );
    }
}
