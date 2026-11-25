package be.twofold.valen.game.greatcircle.reader.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record ResourcesEntry(
    long nameString,
    long depIndices,
    long strings,
    long specialHashes,
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
    short numStrings,
    short numSources,
    short numDependencies,
    short numSpecialHashes
) {
    public static ResourcesEntry read(BinaryReader reader) throws IOException {
        reader.expectLong(0x0); // resourceTypeString
        var nameString = reader.readLong();
        reader.expectLong(-0x1); // descString
        var depIndices = reader.readLong();
        var strings = reader.readLong();
        var specialHashes = reader.readLong();
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
        var numStrings = reader.readShort();
        var numSources = reader.readShort();
        var numDependencies = reader.readShort();
        var numSpecialHashes = reader.readShort();
        reader.expectShort((short) 0x0); // numMetaEntries
        reader.expectInt(0x0); // padding1
        reader.expectShort((short) 0x0); // padding2

        return new ResourcesEntry(
            nameString,
            depIndices,
            strings,
            specialHashes,
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
            numStrings,
            numSources,
            numDependencies,
            numSpecialHashes
        );
    }
}
