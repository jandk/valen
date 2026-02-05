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
    public static ResourcesEntry read(BinarySource source) throws IOException {
        source.expectLong(0x0); // resourceTypeString
        var nameString = source.readLong();
        source.expectLong(-0x1); // descString
        var depIndices = source.readLong();
        var strings = source.readLong();
        var specialHashes = source.readLong();
        source.expectLong(0x0); // metaEntries
        var dataOffset = source.readLong();
        var dataSize = source.readLong();
        var uncompressedSize = source.readLong();
        var dataCheckSum = source.readLong();
        var generationTimeStamp = source.readLong();
        var defaultHash = source.readLong();
        var version = source.readInt();
        var flags = ResourcesFlags.read(source);
        var compMode = ResourcesCompressionMode.read(source);
        source.expectByte((byte) 0x0); // reserved0
        var variation = ResourcesVariation.read(source);
        source.expectInt(0x0); // reserved2
        source.expectLong(0x0); // reservedForVariations
        var numStrings = source.readShort();
        var numSources = source.readShort();
        var numDependencies = source.readShort();
        var numSpecialHashes = source.readShort();
        source.expectShort((short) 0x0); // numMetaEntries
        source.expectInt(0x0); // padding1
        source.expectShort((short) 0x0); // padding2

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
