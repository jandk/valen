package be.twofold.valen.game.doom.resources;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ResourcesIndexEntry(
    int id,
    String name1,
    String name2,
    String name3,
    long offset,
    int size,
    int sizeCompressed,
    int unknown,
    byte patchFileNumber
) {
    public static ResourcesIndexEntry read(DataSource source) throws IOException {
        var id = source.readIntBE();
        var name1 = source.readPString();
        var name2 = source.readPString();
        var name3 = source.readPString();

        var offset = source.readLongBE();
        var size = source.readIntBE();
        var sizeCompressed = source.readIntBE();

        var unknown = source.readInt();
        var patchFileNumber = source.readByte();

        return new ResourcesIndexEntry(
            id,
            name1,
            name2,
            name3,
            offset,
            size,
            sizeCompressed,
            unknown,
            patchFileNumber
        );
    }
}
