package be.twofold.valen.reader.model;

import be.twofold.valen.core.util.*;

import java.util.*;

public record ModelMeshInfo(
    String mtlDecl,
    int unkHash,
    int unknown,
    List<ModelLodInfo> lods
) {
    public static ModelMeshInfo read(BetterBuffer buffer) {
        var mtlDecl = buffer.getString();
        var unkHash = buffer.getInt();
        var unknown = buffer.getInt();
        buffer.expectInt(0);

        var lods = new ArrayList<ModelLodInfo>();
        for (var lod = 0; lod < Model.LodCount; lod++) {
            if (!buffer.getIntAsBool()) {
                lods.add(ModelLodInfo.read(buffer));
            }
        }

        return new ModelMeshInfo(mtlDecl, unkHash, unknown, lods);
    }
}
