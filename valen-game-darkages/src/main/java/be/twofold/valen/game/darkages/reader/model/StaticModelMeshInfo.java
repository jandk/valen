package be.twofold.valen.game.darkages.reader.model;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StaticModelMeshInfo(
    String mtlDecl,
    int unkHash,
    int unknown,
    List<StaticModelLodInfo> lodInfos
) {
    public static StaticModelMeshInfo read(BinaryReader reader) throws IOException {
        var mtlDecl = reader.readString(StringFormat.INT_LENGTH);
        var unkHash = reader.readInt();
        var unknown = reader.readInt();
        reader.expectInt(0);

        var lodInfos = new ArrayList<StaticModelLodInfo>();
        for (var lod = 0; lod < StaticModel.LodCount; lod++) {
            if (!reader.readBool(BoolFormat.INT)) {
                lodInfos.add(StaticModelLodInfo.read(reader));
            }
        }

        return new StaticModelMeshInfo(mtlDecl, unkHash, unknown, lodInfos);
    }
}
