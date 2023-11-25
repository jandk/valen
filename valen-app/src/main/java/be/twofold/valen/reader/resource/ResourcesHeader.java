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
    int addrDependencyEntries,
    int addrDependencyIndexes,
    int addrData,
    int addrEndMarker
) {
    static final int Size = 0x7c;

    public static ResourcesHeader read(BetterBuffer buffer) {
        buffer.expectInt(0x4c434449);
        buffer.expectInt(12);
        buffer.expectInt(0);
        buffer.expectInt(1);
        buffer.expectLong(0xffffffffffL);
        buffer.expectLong(0);

        int numFileEntries = buffer.getInt();
        int numDependencyEntries = buffer.getInt();
        int numDependencyIndexes = buffer.getInt();
        int numPathStringIndexes = buffer.getInt();

        buffer.expectInt(0);
        buffer.expectInt(0);
        int sizeStrings = buffer.getInt();
        buffer.expectInt(0);

        int addrPathStringOffsets = buffer.getLongAsInt();
        int addrErrorLogs = buffer.getLongAsInt();
        buffer.expectLong(0x7c);
        int addrDependencyEntries = buffer.getLongAsInt();
        int addrDependencyIndexes = buffer.getLongAsInt();
        int addrData = buffer.getLongAsInt();
        buffer.expectInt(0);
        int addrEndMarker = buffer.getLongAsInt();

        return new ResourcesHeader(
            numFileEntries,
            numDependencyEntries,
            numDependencyIndexes,
            numPathStringIndexes,
            sizeStrings,
            addrPathStringOffsets,
            addrErrorLogs,
            addrDependencyEntries,
            addrDependencyIndexes,
            addrData,
            addrEndMarker
        );
    }
}
