package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.util.*;

import java.util.*;

public record StaticModelMeshInfo(
    String mtlDecl,
    int unkHash,
    int unknown,
    List<StaticModelLodInfo> lodInfos
) {
    public static StaticModelMeshInfo read(BetterBuffer buffer) {
        var mtlDecl = buffer.getString();
        var unkHash = buffer.getInt();
        var unknown = buffer.getInt();
        buffer.expectInt(0);

        var lodInfos = new ArrayList<StaticModelLodInfo>();
        for (var lod = 0; lod < StaticModel.LodCount; lod++) {
            if (!buffer.getIntAsBool()) {
                lodInfos.add(StaticModelLodInfo.read(buffer));
            }
        }

        return new StaticModelMeshInfo(mtlDecl, unkHash, unknown, lodInfos);
    }
}
