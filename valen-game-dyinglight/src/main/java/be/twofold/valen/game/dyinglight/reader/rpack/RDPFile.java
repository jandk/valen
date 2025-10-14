package be.twofold.valen.game.dyinglight.reader.rpack;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;

public record RDPFile(
    byte numParts,
    byte unk01,
    ResourceType type,
    byte unk03,
    int fileIndex,
    int partIndex
) {
    public static RDPFile read(BinaryReader reader) throws IOException {
        var numParts = reader.readByte();
        var unk01 = reader.readByte();
        var type = ValueEnum.fromValue(ResourceType.class, Byte.toUnsignedInt(reader.readByte()));
        var unk03 = reader.readByte();
        var fileIndex = reader.readInt();
        var partIndex = reader.readInt();

        return new RDPFile(
            numParts,
            unk01,
            type,
            unk03,
            fileIndex,
            partIndex
        );
    }
}
