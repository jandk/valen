package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.math.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

record Md6MeshBoneInfo(
    Shorts jointRemap,
    Bounds defaultBounds,
    Floats maxLodDeviations
) {
    static Md6MeshBoneInfo read(BinarySource source) throws IOException {
        var jointRemap = source.readShorts(source.readShort());
        var defaultBounds = Bounds.read(source);
        source.expectInt(5); // numLods
        int unknown = source.readInt();
        var maxLodDeviations = source.readFloats(5);

        return new Md6MeshBoneInfo(
            jointRemap,
            defaultBounds,
            maxLodDeviations
        );
    }
}
