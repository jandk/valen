package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;
import java.util.*;

record Md6MeshInfo(
    String meshName,
    String materialName,
    int unknown0,
    int unknown1,
    int unknown2,
    int unknown3,
    int unkHash,
    List<Md6MeshLodInfo> lodInfos
) {
    static Md6MeshInfo read(BinaryReader reader) throws IOException {
        var meshName = reader.readPString();
        var materialName = reader.readPString();
        reader.expectByte((byte) 1);
        int unknown0 = reader.readInt();
        var unknown1 = reader.readInt();
        var unknown2 = reader.readInt();
        var unknown3 = reader.readInt();
        var unkHash = reader.readInt();
        reader.expectInt(0);

        var lodInfos = new ArrayList<Md6MeshLodInfo>();
        for (var i = 0; i < 5; i++) {
            if (!reader.readBoolInt()) {
                lodInfos.add(Md6MeshLodInfo.read(reader));
            }
        }
        reader.expectInt(0);

        return new Md6MeshInfo(
            meshName,
            materialName,
            unknown0,
            unknown1,
            unknown2,
            unknown3,
            unkHash,
            lodInfos
        );
    }
}
