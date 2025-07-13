package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.BinaryReader;

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
    public static Md6ModelMeshInfo read(BinaryReader reader, int numLods) throws IOException {
        var meshName = reader.readPString();
        var materialName = reader.readPString();
        var unknown1 = reader.readInt();
        var unknown2 = reader.readInt();
        var unknown3 = reader.readShort();

        var lodInfos = new ArrayList<Md6ModelLodInfo>();
        for (var i = 0; i < numLods; i++) {
            var absent = reader.readBoolInt();
            if (!absent) {
                lodInfos.add(Md6ModelLodInfo.read(reader));
            }
        }
        reader.expectByte((byte) 0); // morphMapPresent
        reader.expectInt(0); // blendShapesPresent

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
