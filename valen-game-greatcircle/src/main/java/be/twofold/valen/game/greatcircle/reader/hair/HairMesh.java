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
    public static HairMesh read(BinaryReader reader) throws IOException {
        var header = HairHeader.read(reader);
        var particleSumPerStrand = reader.readInts(header.numStrands());
        var materials = reader.readObjects(reader.readInt(), BinaryReader::readPString);
        var sourcePositions = reader.readShorts(header.numParticles() * 4);
        var particleInfo = reader.readInts(header.numParticles());
        var segments = reader.readInts(header.numSegments());
        var unknown1 = reader.readShorts(header.numParticles());

        var gpuStrands = (List<HairStrandInfoGPU>) null;
        var particleStrandDistances = (float[]) null;
        var strandRootUVs = (short[]) null;
        if (header.allowSimulation()) {
            gpuStrands = reader.readObjects(header.numStrands(), HairStrandInfoGPU::read);
            particleStrandDistances = reader.readFloats(header.numStrands());
            strandRootUVs = reader.readShorts(header.numStrands() * 2);
        }

        var numBlendShapes = header.numStrands() * header.numBlendShapeLODs();
        var strandBlendShapeInfo = reader.readObjects(numBlendShapes, HairStrandBlendShape::read);

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
