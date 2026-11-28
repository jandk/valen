package be.twofold.valen.game.doom.resources;

import be.twofold.valen.game.doom.readers.*;
import wtf.reversed.toolbox.io.*;

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
    public static ResourcesIndexEntry read(BinaryReader reader) throws IOException {
        var index = reader.readInt();
        var typeName = IdStrings.read(reader);
        var resourceName = IdStrings.read(reader);
        var fileName = IdStrings.read(reader);
        var offset = reader.readLong();
        var size = reader.readInt();
        var sizeCompressed = reader.readInt();
        var unknown = reader.readInt();
        var fileId = reader.readByte();

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
