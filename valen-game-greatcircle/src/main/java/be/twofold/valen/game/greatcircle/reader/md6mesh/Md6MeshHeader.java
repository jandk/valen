package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.BinaryReader;
import be.twofold.valen.core.math.*;

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
        var skeletonName = reader.readPString();
        var faceRigName = reader.readPString();
        var unknownName = reader.readPString();
        var hcRigName = reader.readPString();
        var minBoundsExpansion = Vector3.read(reader);
        var maxBoundsExpansion = Vector3.read(reader);
        var remapForSkinning = reader.readBoolByte(); // true for md6skel, false for alembic

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
