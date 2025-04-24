package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

record Md6MeshInfo(
    String meshName,
    String materialName,
    int unknown1,
    int unknown2,
    int unknown3,
    int unkHash,
    List<Md6MeshLodInfo> lodInfos
) {
    static Md6MeshInfo read(DataSource source) throws IOException {
        var meshName = source.readPString();
        var materialName = source.readPString();
        source.expectByte((byte) 1);
        source.expectInt(1);
        var unknown1 = source.readInt();
        var unknown2 = source.readInt();
        var unknown3 = source.readInt();
        var unkHash = source.readInt();
        source.expectInt(0);

        var lodInfos = new ArrayList<Md6MeshLodInfo>();
        for (var i = 0; i < 5; i++) {
            if (!source.readBoolInt()) {
                lodInfos.add(Md6MeshLodInfo.read(source));
            }
        }
        source.expectInt(0);

        return new Md6MeshInfo(
            meshName,
            materialName,
            unknown1,
            unknown2,
            unknown3,
            unkHash,
            lodInfos
        );
    }
}
