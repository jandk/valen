package be.twofold.valen.reader.md6;

import be.twofold.valen.core.util.*;

import java.util.*;

public record Md6MeshInfo(
    String meshName,
    String materialName,
    int unknown1,
    int unknown2,
    int unknown3,
    int unkHash,
    List<Md6MeshLodInfo> lodInfos
) {
    public static Md6MeshInfo read(BetterBuffer buffer) {
        var meshName = buffer.getString();
        var materialName = buffer.getString();
        buffer.expectByte(1);
        buffer.expectInt(1);
        var unknown1 = buffer.getInt();
        var unknown2 = buffer.getInt();
        var unknown3 = buffer.getInt();
        var unkHash = buffer.getInt();

        var lodInfos = new ArrayList<Md6MeshLodInfo>();
        for (var i = 0; i < 5; i++) {
            if (!buffer.getIntAsBool()) {
                lodInfos.add(Md6MeshLodInfo.read(buffer));
            }
        }

        buffer.expectInt(0);
        buffer.expectByte(0);

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
