package be.twofold.valen.game.greatcircle.reader.deformmodel;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record DeformModelMesh(
    int unknown1,
    Bytes unknown2,
    List<DeformModelLod> lods,
    Ints unknown3
) {
    public static DeformModelMesh read(BinarySource source) throws IOException {
        var unknown1 = source.readInt();
        var unknown2 = Bytes.Mutable.allocate(5);
        var lods = new ArrayList<DeformModelLod>();
        for (int i = 0; i < 5; i++) {
            unknown2.set(i, source.readByte());
            if (unknown2.get(i) != 0) {
                lods.add(DeformModelLod.read(source));
            }
        }
        var unknown3 = source.readInts(source.readInt());
        return new DeformModelMesh(unknown1, unknown2, lods, unknown3);
    }
}
