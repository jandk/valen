package be.twofold.valen.reader.resource;

import be.twofold.valen.core.util.*;

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
    public static final int BYTES = 124;

    public static ResourcesHeader read(BetterBuffer buffer) {
        buffer.expectInt(0x4c434449);
        buffer.expectInt(12);
        buffer.expectInt(0);
        buffer.expectInt(1);
        buffer.expectLong(0xffffffffffL);
        buffer.expectLong(0);

        var numFileEntries = buffer.getInt();
        var numDependencyEntries = buffer.getInt();
        var numDependencyIndexes = buffer.getInt();
        var numPathStringIndexes = buffer.getInt();

        buffer.expectInt(0);
        buffer.expectInt(0);
        var sizeStrings = buffer.getInt();
        buffer.expectInt(0);

        var addrPathStringOffsets = buffer.getLongAsInt();
        var addrErrorLogs = buffer.getLongAsInt();
        var addrFileEntries = buffer.getLongAsInt();
        var addrDependencyEntries = buffer.getLongAsInt();
        var addrDependencyIndexes = buffer.getLongAsInt();
        var addrData = buffer.getLongAsInt();
        buffer.expectInt(0);
        var addrEndMarker = buffer.getLongAsInt();

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
