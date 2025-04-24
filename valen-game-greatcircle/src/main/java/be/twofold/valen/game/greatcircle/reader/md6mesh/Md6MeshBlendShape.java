package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.*;

import java.io.*;

record Md6MeshBlendShape(
    String name,
    short rigControlIndex,
    int deltaIndexStart
) {
    static Md6MeshBlendShape read(DataSource source) throws IOException {
        var name = source.readPString();
        var rigControlIndex = source.readShort();
        var deltaIndexStart = source.readInt();
        return new Md6MeshBlendShape(name, rigControlIndex, deltaIndexStart);
    }
}
