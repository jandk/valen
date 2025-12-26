package be.twofold.valen.game.greatcircle.reader.deformmodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.util.*;

public record DeformModelMesh(
    int unknown1,
    Bytes unknown2,
    List<DeformModelLod> lods,
    Ints unknown3
) {
    public static DeformModelMesh read(BinaryReader reader) throws IOException {
        var unknown1 = reader.readInt();
        var unknown2 = Bytes.Mutable.allocate(5);
        var lods = new ArrayList<DeformModelLod>();
        for (int i = 0; i < 5; i++) {
            unknown2.set(i, reader.readByte());
            if (unknown2.get(i) != 0) {
                lods.add(DeformModelLod.read(reader));
            }
        }
        var unknown3 = reader.readInts(reader.readInt());
        return new DeformModelMesh(unknown1, unknown2, lods, unknown3);
    }
}
