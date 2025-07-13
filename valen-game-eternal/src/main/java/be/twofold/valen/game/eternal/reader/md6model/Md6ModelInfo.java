package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;
import java.util.*;

public record Md6ModelInfo(
    String meshName,
    String materialName,
    int unknown1,
    int unknown2,
    int unknown3,
    int unkHash,
    List<Md6ModelLodInfo> lodInfos
) {
    public static Md6ModelInfo read(BinaryReader reader) throws IOException {
        var meshName = reader.readPString();
        var materialName = reader.readPString();
        reader.expectByte((byte) 1);
        reader.expectInt(1);
        var unknown1 = reader.readInt();
        var unknown2 = reader.readInt();
        var unknown3 = reader.readInt();
        var unkHash = reader.readInt();

        var lodInfos = new ArrayList<Md6ModelLodInfo>();
        for (var i = 0; i < 5; i++) {
            if (!reader.readBoolInt()) {
                lodInfos.add(Md6ModelLodInfo.read(reader));
            }
        }

        reader.expectByte((byte) 0);
        reader.expectInt(0);

        return new Md6ModelInfo(
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
