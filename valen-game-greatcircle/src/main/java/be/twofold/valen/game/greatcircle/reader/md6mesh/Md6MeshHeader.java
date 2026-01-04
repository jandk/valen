package be.twofold.valen.game.greatcircle.reader.md6mesh;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;

record Md6MeshHeader(
    String skeletonName,
    String faceRigName,
    String unknownName,
    String hcRigName,
    Vector3 minBoundsExpansion,
    Vector3 maxBoundsExpansion,
    boolean remapForSkinning
) {
    static Md6MeshHeader read(BinarySource source) throws IOException {
        var skeletonName = source.readString(StringFormat.INT_LENGTH);
        var faceRigName = source.readString(StringFormat.INT_LENGTH);
        var unknownName = source.readString(StringFormat.INT_LENGTH);
        var hcRigName = source.readString(StringFormat.INT_LENGTH);
        var minBoundsExpansion = Vector3.read(source);
        var maxBoundsExpansion = Vector3.read(source);
        var remapForSkinning = source.readBool(BoolFormat.BYTE); // true for md6skel, false for alembic

        return new Md6MeshHeader(
            skeletonName,
            faceRigName,
            unknownName,
            hcRigName,
            minBoundsExpansion,
            maxBoundsExpansion,
            remapForSkinning
        );
    }
}
