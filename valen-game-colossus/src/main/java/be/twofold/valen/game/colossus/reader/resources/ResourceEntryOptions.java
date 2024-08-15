package be.twofold.valen.game.colossus.reader.resources;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ResourceEntryOptions(
    long uncompressedSize,
    long dataCheckSum,
    long generationTimeStamp,
    long defaultHash,
    int version,
    int flags,
    byte compMode,
    short variation
) {
    public static ResourceEntryOptions read(DataSource source) throws IOException {
        var uncompressedSize = source.readLong();
        var dataCheckSum = source.readLong();
        var generationTimeStamp = source.readLong();
        var defaultHash = source.readLong();
        var version = source.readInt();
        var flags = source.readInt();
        var compMode = source.readByte();
        source.expectByte((byte) 0); // reserved0
        var variation = source.readShort();
        source.expectInt(0); // reserved2
        source.expectLong(0); // reservedForVariations

        return new ResourceEntryOptions(
            uncompressedSize,
            dataCheckSum,
            generationTimeStamp,
            defaultHash,
            version,
            flags,
            compMode,
            variation
        );
    }
}
