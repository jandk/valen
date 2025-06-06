package be.twofold.valen.game.darkages.reader.strandshair;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

record StrandsHairHeader(
    int version,
    int numStrands,
    int numParticles,
    int numSegments,
    int numBlendShapeLODs,
    Vector3 compressionPosBias,
    float compressionPosScale,
    float strandThickness,
    int unknown,
    Bounds bounds,
    Bounds tightBounds
) {
    static StrandsHairHeader read(DataSource source) throws IOException {
        var version = source.readInt();
        var numStrands = source.readInt();
        var numParticles = source.readInt();
        var numSegments = source.readInt();
        var numBlendShapeLODs = source.readInt();
        var compressionPosBias = Vector3.read(source);
        var compressionPosScale = source.readFloat();
        var strandThickness = source.readFloat();
        var unknown = source.readInt();
        var bounds = Bounds.read(source);
        var tightBounds = Bounds.read(source);

        return new StrandsHairHeader(
            version,
            numStrands,
            numParticles,
            numSegments,
            numBlendShapeLODs,
            compressionPosBias,
            compressionPosScale,
            strandThickness,
            unknown,
            bounds,
            tightBounds
        );
    }
}
