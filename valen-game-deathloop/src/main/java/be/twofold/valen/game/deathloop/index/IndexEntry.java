package be.twofold.valen.game.deathloop.index;

import be.twofold.valen.core.io.*;

import java.io.*;

public record IndexEntry(
    int index,
    String typeName,
    String resourceName,
    String fileName,
    int offset,
    int uncompressedLength,
    int compressedLength,
    int useBits,
    short fileId
) {
    public static IndexEntry read(DataSource source) throws IOException {
        var index = source.readInt();
        var typeName = source.readPString();
        var resourceName = source.readPString();
        var fileName = source.readPString();
        var offset = source.readLongAsInt();
        var uncompressedLength = source.readInt();
        var compressedLength = source.readInt();
        source.expectInt(0);
        source.expectInt(0);
        var useBits = source.readInt();
        var fileId = source.readShort();

        return new IndexEntry(
            index,
            typeName,
            resourceName,
            fileName,
            offset,
            uncompressedLength,
            compressedLength,
            useBits,
            fileId
        );
    }
}
