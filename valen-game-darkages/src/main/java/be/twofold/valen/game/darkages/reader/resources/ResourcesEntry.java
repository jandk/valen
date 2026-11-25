package be.twofold.valen.game.darkages.reader.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record ResourcesEntry(
    long depIndices,
    long strings,
    long dataOffset,
    long dataSize,
    long uncompressedSize,
    long dataCheckSum,
    long generationTimeStamp,
    long defaultHash,
    int version,
    Set<ResourcesFlags> flags,
    ResourcesCompressionMode compMode,
    ResourcesVariation variation,
    short numSources,
    short numDependencies
) {
    public static ResourcesEntry read(BinaryReader reader) throws IOException {
        reader.expectLong(0x0); // resourceTypeString
        reader.expectLong(0x1); // nameString
        reader.expectLong(-0x1); // descString
        var depIndices = reader.readLong();
        var strings = reader.readLong();
        reader.expectLong(0x0); // specialHashes
        reader.expectLong(0x0); // metaEntries
        var dataOffset = reader.readLong();
        var dataSize = reader.readLong();
        var uncompressedSize = reader.readLong();
        var dataCheckSum = reader.readLong();
        var generationTimeStamp = reader.readLong();
        var defaultHash = reader.readLong();
        var version = reader.readInt();
        var flags = ResourcesFlags.read(reader);
        var compMode = ResourcesCompressionMode.read(reader);
        reader.expectByte((byte) 0x0); // reserved0
        var variation = ResourcesVariation.read(reader);
        reader.expectInt(0x0); // reserved2
        reader.expectLong(0x0); // reservedForVariations
        reader.expectShort((short) 0x2); // numStrings
        var numSources = reader.readShort();
        var numDependencies = reader.readShort();
        reader.expectShort((short) 0x0); // numSpecialHashes
        reader.expectShort((short) 0x0); // numMetaEntries
        reader.expectInt(0x0); // padding1
        reader.expectShort((short) 0x0); // padding2

        return new ResourcesEntry(
            depIndices,
            strings,
            dataOffset,
            dataSize,
            uncompressedSize,
            dataCheckSum,
            generationTimeStamp,
            defaultHash,
            version,
            flags,
            compMode,
            variation,
            numSources,
            numDependencies
        );
    }
}
