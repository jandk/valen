package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.*;

import java.io.*;

record Md6MeshBlendShape(
    String name,
    short rigControlIndex,
    int deltaIndexStart
) {
    static Md6MeshBlendShape read(BinarySource source) throws IOException {
        var name = source.readString(StringFormat.INT_LENGTH);
        var rigControlIndex = source.readShort();
        var deltaIndexStart = source.readInt();
        return new Md6MeshBlendShape(name, rigControlIndex, deltaIndexStart);
    }
}
