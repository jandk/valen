package be.twofold.valen.reader.file.mapresources;

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
        int type = Integer.reverseBytes(source.readInt());
        String name = source.readPString();
        int unknown1 = Integer.reverseBytes(source.readInt());
        int unknown2 = Integer.reverseBytes(source.readInt());
        int unknown3 = Integer.reverseBytes(source.readInt());
        source.expectInt(0);
        source.expectInt(0);
        int unknown4 = Integer.reverseBytes(source.readInt());
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
