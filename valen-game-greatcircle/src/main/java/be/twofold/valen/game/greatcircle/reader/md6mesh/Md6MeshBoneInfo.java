package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

record Md6MeshBoneInfo(
    short[] jointRemap,
    Bounds defaultBounds,
    float[] maxLodDeviations
) {
    static Md6MeshBoneInfo read(DataSource source) throws IOException {
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
