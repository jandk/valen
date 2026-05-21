package be.twofold.valen.game.greatcircle.reader.md6mesh;

import wtf.reversed.toolbox.io.*;

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
    static Md6MeshInfo read(BinarySource source) throws IOException {
        var meshName = source.readString(StringFormat.INT_LENGTH);
        var materialName = source.readString(StringFormat.INT_LENGTH);
        source.expectByte((byte) 1);
        int unknown0 = source.readInt();
        var unknown1 = source.readInt();
        var unknown2 = source.readInt();
        var unknown3 = source.readInt();
        var unkHash = source.readInt();
        source.expectInt(0);

        var lodInfos = new ArrayList<Md6MeshLodInfo>();
        for (var i = 0; i < 5; i++) {
            if (!source.readBool(BoolFormat.INT)) {
                lodInfos.add(Md6MeshLodInfo.read(source));
            }
        }
        source.expectInt(0);

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
