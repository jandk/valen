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
    List<Md6LodInfo> lodInfos
) {
    public static Md6MeshInfo read(BetterBuffer buffer) {
        String meshName = buffer.getString();
        String materialName = buffer.getString();
        buffer.expectByte(1);
        buffer.expectInt(1);
        int unknown1 = buffer.getInt();
        int unknown2 = buffer.getInt();
        int unknown3 = buffer.getInt();
        int unkHash = buffer.getInt();

        List<Md6LodInfo> lodInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (!buffer.getIntAsBool()) {
                lodInfos.add(Md6LodInfo.read(buffer));
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
