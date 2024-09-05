package be.twofold.valen.game.neworder.index;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record IndexEntry(
    int resourceIndex,
    String typeName,
    String resourceName,
    String fileName,
    int offset,
    int uncompressedLength,
    int compressedLength,
    List<PlatformStreamData> platformStreamData,
    int useBits,
    byte fileId
) {
    public static IndexEntry read(DataSource source) throws IOException {
        var resourceIndex = source.readIntBE();
        var typeName = source.readPString();
        var resourceName = source.readPString();
        var fileName = source.readPString();
        var offset = source.readIntBE();
        var uncompressedLength = source.readIntBE();
        var compressedLength = source.readIntBE();
        var numPlatformStreamData = source.readIntBE();
        var platformStreamData = source.readStructs(numPlatformStreamData, PlatformStreamData::read);
        var useBits = source.readIntBE();
        var fileId = source.readByte();

        return new IndexEntry(
            resourceIndex,
            typeName,
            resourceName,
            fileName,
            offset,
            uncompressedLength,
            compressedLength,
            platformStreamData,
            useBits,
            fileId
        );
    }
}
