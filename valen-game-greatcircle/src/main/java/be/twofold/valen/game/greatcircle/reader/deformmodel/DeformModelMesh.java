package be.twofold.valen.game.greatcircle.reader.deformmodel;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record DeformModelMesh(
    int unknown1,
    byte[] unknown2,
    List<DeformModelLod> lods,
    int[] unknown3
) {
    public static DeformModelMesh read(DataSource source) throws IOException {
        var unknown1 = source.readInt();
        var unknown2 = new byte[5];
        var lods = new ArrayList<DeformModelLod>(5);
        for (int i = 0; i < 5; i++) {
            unknown2[i] = source.readByte();
            if (unknown2[i] != 0) {
                lods.add(DeformModelLod.read(source));
            }
        }
        var unknown3 = source.readInts(source.readInt());
        return new DeformModelMesh(unknown1, unknown2, lods, unknown3);
    }
}
