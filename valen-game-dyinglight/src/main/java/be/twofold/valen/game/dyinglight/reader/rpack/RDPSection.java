package be.twofold.valen.game.dyinglight.reader.rpack;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;

public record RDPSection(
    ResourceType type,
    byte unk01,
    byte unk02,
    byte unk03,
    int offset,
    int size,
    int numAssets
) {
    public static RDPSection read(BinaryReader reader) throws IOException {
        var type = ValueEnum.fromValue(ResourceType.class, Byte.toUnsignedInt(reader.readByte()));
        var unk01 = reader.readByte();
        var unk02 = reader.readByte();
        var unk03 = reader.readByte();
        var offset = reader.readInt();
        var size = reader.readLongAsInt();
        var numAssets = reader.readInt();

        return new RDPSection(
            type,
            unk01,
            unk02,
            unk03,
            offset,
            size,
            numAssets
        );
    }
}
