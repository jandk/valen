package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.*;
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
    static Md6MeshHeader read(DataSource source) throws IOException {
        var skeletonName = source.readPString();
        var faceRigName = source.readPString();
        var unknownName = source.readPString();
        var hcRigName = source.readPString();
        var minBoundsExpansion = Vector3.read(source);
        var maxBoundsExpansion = Vector3.read(source);
        var remapForSkinning = source.readBoolByte(); // true for md6skel, false for alembic

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
