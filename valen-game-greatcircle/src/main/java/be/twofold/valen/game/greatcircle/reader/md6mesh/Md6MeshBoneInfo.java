package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.BinaryReader;
import be.twofold.valen.core.math.*;

import java.io.*;

record Md6MeshBoneInfo(
    short[] jointRemap,
    Bounds defaultBounds,
    float[] maxLodDeviations
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
