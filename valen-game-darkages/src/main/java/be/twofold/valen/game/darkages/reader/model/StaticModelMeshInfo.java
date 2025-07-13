package be.twofold.valen.game.darkages.reader.model;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StaticModelMeshInfo(
    String mtlDecl,
    int unkHash,
    int unknown,
    List<StaticModelLodInfo> lodInfos
) {
    public static StaticModelMeshInfo read(BinaryReader reader) throws IOException {
        var mtlDecl = reader.readPString();
        var unkHash = reader.readInt();
        var unknown = reader.readInt();
        reader.expectInt(0);

        var lodInfos = new ArrayList<StaticModelLodInfo>();
        for (var lod = 0; lod < StaticModel.LodCount; lod++) {
            if (!reader.readBoolInt()) {
                lodInfos.add(StaticModelLodInfo.read(reader));
            }
        }

        return new StaticModelMeshInfo(mtlDecl, unkHash, unknown, lodInfos);
    }
}
