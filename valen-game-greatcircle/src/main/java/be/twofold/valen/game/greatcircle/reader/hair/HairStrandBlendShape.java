package be.twofold.valen.game.greatcircle.reader.hair;

import be.twofold.valen.core.io.*;

import java.io.*;

public record HairStrandBlendShape(
    int idxTriangle,
    int barycentrics,
    int triangleNormal,
    int triangleSide
) {
    public static HairStrandBlendShape read(DataSource source) throws IOException {
        var idxTriangle = source.readInt();
        var barycentrics = source.readInt();
        var triangleNormal = source.readInt();
        var triangleSide = source.readInt();
        return new HairStrandBlendShape(idxTriangle, barycentrics, triangleNormal, triangleSide);
    }
}
