package be.twofold.valen.game.greatcircle.reader.md6mesh;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;

record Md6MeshBoneInfo(
    Shorts jointRemap,
    Bounds defaultBounds,
    Floats maxLodDeviations
) {
    static Md6MeshBoneInfo read(BinaryReader reader) throws IOException {
        var jointRemap = reader.readShorts(reader.readShort());
        var defaultBounds = Bounds.read(reader);
        reader.expectInt(5); // numLods
        int unknown = reader.readInt();
        var maxLodDeviations = reader.readFloats(5);

        return new Md6MeshBoneInfo(
            jointRemap,
            defaultBounds,
            maxLodDeviations
        );
    }
}
