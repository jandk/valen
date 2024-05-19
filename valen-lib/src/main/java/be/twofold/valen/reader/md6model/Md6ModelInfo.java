package be.twofold.valen.reader.md6model;

import be.twofold.valen.core.io.*;

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
    public static Md6ModelInfo read(DataSource source) throws IOException {
        var meshName = source.readPString();
        var materialName = source.readPString();
        source.expectByte((byte) 1);
        source.expectInt(1);
        var unknown1 = source.readInt();
        var unknown2 = source.readInt();
        var unknown3 = source.readInt();
        var unkHash = source.readInt();

        var lodInfos = new ArrayList<Md6ModelLodInfo>();
        for (var i = 0; i < 5; i++) {
            if (!source.readBoolInt()) {
                lodInfos.add(Md6ModelLodInfo.read(source));
            }
        }

        source.expectByte((byte) 0);
        source.expectInt(0);

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
