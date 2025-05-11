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
    public static HairHeader read(DataSource source) throws IOException {
        var numStrands = source.readInt();
        var numParticles = source.readInt();
        var numSegments = source.readInt();
        var numBlendShapeLODs = source.readInt();
        var compressionPosBias = Vector3.read(source);
        var compressionPosScale = source.readFloat();
        var blendShapeMatchMeshName = source.readPString();
        var strandThickness = source.readFloat();
        var averageSegmentLength = source.readFloat();
        var allowSimulation = source.readBoolByte();
        var bounds = Bounds.read(source);
        var tightBounds = Bounds.read(source);

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
