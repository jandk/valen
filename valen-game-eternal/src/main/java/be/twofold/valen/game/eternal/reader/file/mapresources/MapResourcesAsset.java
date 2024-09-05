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
    public static MapResourcesAsset read(DataSource source) throws IOException {
        int type = source.readIntBE();
        String name = source.readPString();
        int unknown1 = source.readIntBE();
        int unknown2 = source.readIntBE();
        int unknown3 = source.readIntBE();
        source.expectInt(0);
        source.expectInt(0);
        int unknown4 = source.readIntBE();
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
