package be.twofold.valen.game.colossus.reader.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ResourceEntry(
    int depIndices,
    int strings,
    long dataOffset,
    int dataSize,
    ResourceEntryOptions options,
    short numStrings,
    short numDependencies
) {
    public static ResourceEntry read(BinarySource source) throws IOException {
        source.expectLong(0); // resourceTypeString
        source.expectLong(1); // nameString
        source.expectLong(-1); // descString
        var depIndices = source.readLongAsInt();
        var strings = source.readLongAsInt();
        source.expectLong(0); // specialHashes
        source.expectLong(0); // metaEntries
        var dataOffset = source.readLong();
        var dataSize = source.readLongAsInt();
        var options = ResourceEntryOptions.read(source);
        var numStrings = source.readShort();
        source.expectShort((short) 0); // numSources
        var numDependencies = source.readShort();
        source.expectShort((short) 0); // numSpecialHashes
        source.expectShort((short) 0); // numMetaEntries
        source.skip(6);

        return new ResourceEntry(
            depIndices,
            strings,
            dataOffset,
            dataSize,
            options,
            numStrings,
            numDependencies
        );
    }
}
