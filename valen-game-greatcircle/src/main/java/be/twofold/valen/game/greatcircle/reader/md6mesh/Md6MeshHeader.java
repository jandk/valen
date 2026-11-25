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
    static Md6MeshHeader read(BinaryReader reader) throws IOException {
        var skeletonName = reader.readString(StringFormat.INT_LENGTH);
        var faceRigName = reader.readString(StringFormat.INT_LENGTH);
        var unknownName = reader.readString(StringFormat.INT_LENGTH);
        var hcRigName = reader.readString(StringFormat.INT_LENGTH);
        var minBoundsExpansion = Vector3.read(reader);
        var maxBoundsExpansion = Vector3.read(reader);
        var remapForSkinning = reader.readBool(BoolFormat.BYTE); // true for md6skel, false for alembic

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
