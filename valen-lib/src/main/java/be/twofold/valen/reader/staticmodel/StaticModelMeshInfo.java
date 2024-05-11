package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StaticModelMeshInfo(
    String mtlDecl,
    int unkHash,
    int unknown,
    List<StaticModelLodInfo> lodInfos
) {
    public static StaticModelMeshInfo read(DataSource source) throws IOException {
        var mtlDecl = source.readPString();
        var unkHash = source.readInt();
        var unknown = source.readInt();
        source.expectInt(0);

        var lodInfos = new ArrayList<StaticModelLodInfo>();
        for (var lod = 0; lod < StaticModel.LodCount; lod++) {
            if (!source.readBoolInt()) {
                lodInfos.add(StaticModelLodInfo.read(source));
            }
        }

        return new StaticModelMeshInfo(mtlDecl, unkHash, unknown, lodInfos);
    }
}
