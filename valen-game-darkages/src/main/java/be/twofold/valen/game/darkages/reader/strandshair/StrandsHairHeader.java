package be.twofold.valen.game.darkages.reader.strandshair;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

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
    static StrandsHairHeader read(BinaryReader reader) throws IOException {
        var version = reader.readInt();
        var numStrands = reader.readInt();
        var numParticles = reader.readInt();
        var numSegments = reader.readInt();
        var numBlendShapeLODs = reader.readInt();
        var compressionPosBias = Vector3.read(reader);
        var compressionPosScale = reader.readFloat();
        var strandThickness = reader.readFloat();
        var unknown = reader.readInt();
        var bounds = Bounds.read(reader);
        var tightBounds = Bounds.read(reader);

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
