package be.twofold.valen.game.greatcircle.reader.hair;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record HairHeader(
    int numStrands,
    int numParticles,
    int numSegments,
    int numBlendShapeLODs,
    Vector3 compressionPosBias,
    float compressionPosScale,
    String blendShapeMatchMeshName,
    float strandThickness,
    float averageSegmentLength,
    boolean allowSimulation,
    Bounds bounds,
    Bounds tightBounds
) {
    public static HairHeader read(BinaryReader reader) throws IOException {
        var numStrands = reader.readInt();
        var numParticles = reader.readInt();
        var numSegments = reader.readInt();
        var numBlendShapeLODs = reader.readInt();
        var compressionPosBias = Vector3.read(reader);
        var compressionPosScale = reader.readFloat();
        var blendShapeMatchMeshName = reader.readPString();
        var strandThickness = reader.readFloat();
        var averageSegmentLength = reader.readFloat();
        var allowSimulation = reader.readBoolByte();
        var bounds = Bounds.read(reader);
        var tightBounds = Bounds.read(reader);

        return new HairHeader(
            numStrands,
            numParticles,
            numSegments,
            numBlendShapeLODs,
            compressionPosBias,
            compressionPosScale,
            blendShapeMatchMeshName,
            strandThickness,
            averageSegmentLength,
            allowSimulation,
            bounds,
            tightBounds
        );
    }
}
