package be.twofold.valen.game.eternal.reader.resource;

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
    public static ResourcesHeader read(DataSource source) throws IOException {
        source.expectInt(0x4c434449);
        source.expectInt(12);
        source.expectInt(0);
        source.expectInt(1);
        source.expectLong(0xffffffffffL);
        source.expectLong(0);

        var numFileEntries = source.readInt();
        var numDependencyEntries = source.readInt();
        var numDependencyIndexes = source.readInt();
        var numPathStringIndexes = source.readInt();

        source.expectInt(0);
        source.expectInt(0);
        var sizeStrings = source.readInt();
        source.expectInt(0);

        var addrPathStringOffsets = source.readLongAsInt();
        var addrErrorLogs = source.readLongAsInt();
        var addrFileEntries = source.readLongAsInt();
        var addrDependencyEntries = source.readLongAsInt();
        var addrDependencyIndexes = source.readLongAsInt();
        var addrData = source.readLongAsInt();
        source.expectInt(0);
        var addrEndMarker = source.readLongAsInt();

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
