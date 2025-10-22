package be.twofold.valen.game.dyinglight.reader.rpack;

import be.twofold.valen.core.io.*;

import java.io.*;

public record RDPPart(
    byte sectionIndex,
    byte unk1,
    short fileIndex,
    int offset,
    int size
) {
    public static RDPPart read(BinaryReader reader) throws IOException {
        var sectionIndex = reader.readByte();
        // reader.expectByte((byte) 1); // compression?
        var unk1 = reader.readByte(); // compression?
        var fileIndex = reader.readShort();
        var offset = reader.readInt();
        var size = reader.readLongAsInt();

        return new RDPPart(
            sectionIndex,
            unk1,
            fileIndex,
            offset,
            size
        );
    }
}
