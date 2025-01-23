package be.twofold.valen.game.doom.resources;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ResourcesIndexEntry(
    int index,
    String typeName,
    String resourceName,
    String fileName,
    long offset,
    int size,
    int sizeCompressed,
    int unknown,
    byte fileId
) {
    public static ResourcesIndexEntry read(DataSource source) throws IOException {
        var index = source.readIntBE();
        var typeName = source.readPString();
        var resourceName = source.readPString();
        var fileName = source.readPString();

        var offset = source.readLongBE();
        var size = source.readIntBE();
        var sizeCompressed = source.readIntBE();

        var unknown = source.readInt();
        var fileId = source.readByte();

        return new ResourcesIndexEntry(
            index,
            typeName,
            resourceName,
            fileName,
            offset,
            size,
            sizeCompressed,
            unknown,
            fileId
        );
    }
}
