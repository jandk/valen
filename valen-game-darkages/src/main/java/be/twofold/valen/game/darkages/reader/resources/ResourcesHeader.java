package be.twofold.valen.game.darkages.reader.resources;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;

public record ResourcesHeader(
    int numFileEntries,
    int numDependencyEntries,
    int numDependencyIndexes,
    int numPathStringIndexes,
    int sizeStrings,
    int addrPathStringOffsets,
    int addrErrorLogs,
    int addrFileEntries,
    int addrDependencyEntries,
    int addrDependencyIndexes,
    int addrData
) {
    public static ResourcesHeader read(BinaryReader reader) throws IOException {
        reader.expectInt(0x4c434449); // magic
        reader.expectInt(13); // version
        reader.expectInt(0); // flags
        reader.expectInt(1); // numSegments
        reader.expectLong(0xffffffffffL); // segmentSize
        reader.expectLong(0); // metadataHash

        var numFileEntries = reader.readInt();
        var numDependencyEntries = reader.readInt();
        var numDependencyIndexes = reader.readInt();
        var numPathStringIndexes = reader.readInt();

        reader.expectInt(0);
        reader.expectInt(0);
        var sizeStrings = reader.readInt();
        reader.expectInt(0);

        var addrPathStringOffsets = reader.readLongAsInt();
        var addrErrorLogs = reader.readLongAsInt();
        var addrFileEntries = reader.readLongAsInt();
        var addrDependencyEntries = reader.readLongAsInt();
        var addrDependencyIndexes = reader.readLongAsInt();
        var addrData = reader.readLongAsInt();

        return new ResourcesHeader(
            numFileEntries,
            numDependencyEntries,
            numDependencyIndexes,
            numPathStringIndexes,
            sizeStrings,
            addrPathStringOffsets,
            addrErrorLogs,
            addrFileEntries,
            addrDependencyEntries,
            addrDependencyIndexes,
            addrData
        );
    }
}
