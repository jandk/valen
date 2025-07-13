package be.twofold.valen.game.greatcircle.reader.resources;

import be.twofold.valen.core.io.*;

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
    int addrData,
    int addrEndMarker
) {
    public static ResourcesHeader read(BinaryReader reader) throws IOException {
        reader.expectInt(0x4C434449);
        reader.expectInt(13); // version
        reader.readLong(); // unknown
        reader.readInt(); // unknown
        reader.expectInt(0); // flags
        reader.expectInt(1); // numSegments
        reader.expectLong(0xFFFFFFFFFFL);

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
        reader.expectInt(0);
        var addrEndMarker = reader.readLongAsInt();
        reader.readLong(); // unknown

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
            addrData,
            addrEndMarker
        );
    }
}
