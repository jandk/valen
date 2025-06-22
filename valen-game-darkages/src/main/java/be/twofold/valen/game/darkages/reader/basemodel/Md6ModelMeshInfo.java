package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record Md6ModelMeshInfo(
    String meshName,
    String materialName,
    int unknown1,
    int unknown2,
    short unknown3,
    List<Md6ModelLodInfo> lodInfos
) {
    public static Md6ModelMeshInfo read(DataSource source, int numLods) throws IOException {
        var meshName = source.readPString();
        var materialName = source.readPString();
        var unknown1 = source.readInt();
        var unknown2 = source.readInt();
        var unknown3 = source.readShort();

        var lodInfos = new ArrayList<Md6ModelLodInfo>();
        for (var i = 0; i < numLods; i++) {
            var absent = source.readBoolInt();
            if (!absent) {
                lodInfos.add(Md6ModelLodInfo.read(source));
            }
        }
        source.expectByte((byte) 0); // morphMapPresent
        source.expectInt(0); // blendShapesPresent

        return new Md6ModelMeshInfo(
            meshName,
            materialName,
            unknown1,
            unknown2,
            unknown3,
            lodInfos
        );
    }
}
