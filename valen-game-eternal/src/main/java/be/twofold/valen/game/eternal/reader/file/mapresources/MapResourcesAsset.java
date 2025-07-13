package be.twofold.valen.game.eternal.reader.file.mapresources;

import be.twofold.valen.core.io.*;

import java.io.*;

public record MapResourcesAsset(
    int type,
    String name,
    int unknown1,
    int unknown2,
    int unknown3,
    int unknown4
) {
    public static MapResourcesAsset read(BinaryReader reader) throws IOException {
        int type = reader.readIntBE();
        String name = reader.readPString();
        int unknown1 = reader.readIntBE();
        int unknown2 = reader.readIntBE();
        int unknown3 = reader.readIntBE();
        reader.expectInt(0);
        reader.expectInt(0);
        int unknown4 = reader.readIntBE();
        return new MapResourcesAsset(
            type,
            name,
            unknown1,
            unknown2,
            unknown3,
            unknown4
        );
    }
}
