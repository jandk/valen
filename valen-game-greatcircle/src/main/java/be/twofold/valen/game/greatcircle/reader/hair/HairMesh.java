package be.twofold.valen.game.greatcircle.reader.hair;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record HairMesh(
    HairHeader header,
    int[] particleSumPerStrand,
    List<String> materials,
    short[] sourcePositions,
    int[] particleInfo,
    int[] segments,
    short[] unknown1,
    List<HairStrandInfoGPU> gpuStrands,
    float[] particleStrandDistances,
    short[] strandRootUVs,
    List<HairStrandBlendShape> strandBlendShapeInfo
) {
    public static HairMesh read(DataSource source) throws IOException {
        var header = HairHeader.read(source);
        var particleSumPerStrand = source.readInts(header.numStrands());
        var materials = source.readObjects(source.readInt(), DataSource::readPString);
        var sourcePositions = source.readShorts(header.numParticles() * 4);
        var particleInfo = source.readInts(header.numParticles());
        var segments = source.readInts(header.numSegments());
        var unknown1 = source.readShorts(header.numParticles());

        var gpuStrands = (List<HairStrandInfoGPU>) null;
        var particleStrandDistances = (float[]) null;
        var strandRootUVs = (short[]) null;
        if (header.allowSimulation()) {
            gpuStrands = source.readObjects(header.numStrands(), HairStrandInfoGPU::read);
            particleStrandDistances = source.readFloats(header.numStrands());
            strandRootUVs = source.readShorts(header.numStrands() * 2);
        }

        var numBlendShapes = header.numStrands() * header.numBlendShapeLODs();
        var strandBlendShapeInfo = source.readObjects(numBlendShapes, HairStrandBlendShape::read);

        return new HairMesh(
            header,
            particleSumPerStrand,
            materials,
            sourcePositions,
            particleInfo,
            segments,
            unknown1,
            gpuStrands,
            particleStrandDistances,
            strandRootUVs,
            strandBlendShapeInfo
        );
    }
}
