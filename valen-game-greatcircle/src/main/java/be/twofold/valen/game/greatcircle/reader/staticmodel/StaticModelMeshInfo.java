package be.twofold.valen.game.greatcircle.reader.staticmodel;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StaticModelMeshInfo(
    String mtlDecl,
    int unkHash,
    List<StaticModelLodInfo> lodInfos
) {
    public static StaticModelMeshInfo read(BinaryReader reader, int version) throws IOException {
        var mtlDecl = reader.readPString();
        var unkHash = version < 80 ? reader.readInt() : 0;
        var unknown = reader.readInt();
        if (unknown != 0 && unknown != -1 && unknown != 1) {
            throw new IOException("Unknown value: " + unknown);
        }
        reader.expectInt(0);

        var lodInfos = new ArrayList<StaticModelLodInfo>();
        for (var lod = 0; lod < StaticModel.LodCount; lod++) {
            if (!reader.readBoolInt()) {
                lodInfos.add(StaticModelLodInfo.read(reader));
            }
        }

        return new StaticModelMeshInfo(mtlDecl, unkHash, lodInfos);
    }
}
