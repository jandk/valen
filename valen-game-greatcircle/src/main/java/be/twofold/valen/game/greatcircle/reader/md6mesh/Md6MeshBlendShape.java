package be.twofold.valen.game.greatcircle.reader.md6mesh;

import wtf.reversed.toolbox.io.*;

import java.io.*;

record Md6MeshBlendShape(
    String name,
    short rigControlIndex,
    int deltaIndexStart
) {
    static Md6MeshBlendShape read(BinaryReader reader) throws IOException {
        var name = reader.readString(StringFormat.INT_LENGTH);
        var rigControlIndex = reader.readShort();
        var deltaIndexStart = reader.readInt();
        return new Md6MeshBlendShape(name, rigControlIndex, deltaIndexStart);
    }
}
