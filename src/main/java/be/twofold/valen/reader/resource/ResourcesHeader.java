package be.twofold.valen.reader.resource;

import java.nio.*;

public record ResourcesHeader(
    int numFileEntries,
    int numDependencyEntries,
    int numDependencyIndexes,
    int numPathStringIndexes,
    int sizeStrings,
    long addrPathStringOffsets,
    long addrDependencyEntries,
    long addrDependencyIndexes,
    long addrData,
    long addrEndMarker
) {
    private static final int Magic = 0x4c434449;
    static final int Size = 0x7c;

    public static ResourcesHeader read(ByteBuffer buffer) {
        int magic = buffer.getInt(0x00);
        if (magic != Magic) {
            throw new IllegalArgumentException("Invalid magic, expected 0x%08x, got 0x%08x".formatted(Magic, magic));
        }

        int numFileEntries = buffer.getInt(0x20);
        int numDependencyEntries = buffer.getInt(0x24);
        int numDependencyIndexes = buffer.getInt(0x28);
        int numPathStringIndexes = buffer.getInt(0x2c);
        int sizeStrings = buffer.getInt(0x38);
        long addrPathStringOffsets = buffer.getLong(0x40);
        long addrDependencyEntries = buffer.getLong(0x58);
        long addrDependencyIndexes = buffer.getLong(0x60);
        long addrData = buffer.getLong(0x68);
        long addrEndMarker = buffer.getLong(0x74);

        return new ResourcesHeader(
            numFileEntries,
            numDependencyEntries,
            numDependencyIndexes,
            numPathStringIndexes,
            sizeStrings,
            addrPathStringOffsets,
            addrDependencyEntries,
            addrDependencyIndexes,
            addrData,
            addrEndMarker
        );
    }
}
