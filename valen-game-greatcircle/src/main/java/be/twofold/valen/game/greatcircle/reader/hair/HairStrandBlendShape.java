package be.twofold.valen.game.greatcircle.reader.hair;

import be.twofold.valen.core.io.*;

import java.io.*;

public record HairStrandBlendShape(
    int idxTriangle,
    int barycentrics,
    int triangleNormal,
    int triangleSide
) {
    public static HairStrandBlendShape read(BinaryReader reader) throws IOException {
        var idxTriangle = reader.readInt();
        var barycentrics = reader.readInt();
        var triangleNormal = reader.readInt();
        var triangleSide = reader.readInt();
        return new HairStrandBlendShape(idxTriangle, barycentrics, triangleNormal, triangleSide);
    }
}
