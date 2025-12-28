package be.twofold.valen.game.greatcircle.reader.staticmodel;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StaticModelMeshInfo(
    String mtlDecl,
    int unkHash,
    List<StaticModelLodInfo> lodInfos
) {
    public static StaticModelMeshInfo read(BinarySource source, int version) throws IOException {
        var mtlDecl = source.readString(StringFormat.INT_LENGTH);
        var unkHash = version < 80 ? source.readInt() : 0;
        var unknown = source.readInt();
        if (unknown != 0 && unknown != -1 && unknown != 1) {
            throw new IOException("Unknown value: " + unknown);
        }
        source.expectInt(0);

        var lodInfos = new ArrayList<StaticModelLodInfo>();
        for (var lod = 0; lod < StaticModel.LodCount; lod++) {
            if (!source.readBool(BoolFormat.INT)) {
                lodInfos.add(StaticModelLodInfo.read(source));
            }
        }

        return new StaticModelMeshInfo(mtlDecl, unkHash, lodInfos);
    }
}
