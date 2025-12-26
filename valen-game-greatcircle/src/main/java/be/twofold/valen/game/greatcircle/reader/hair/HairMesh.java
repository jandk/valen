package be.twofold.valen.game.greatcircle.reader.hair;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record HairMesh(
    HairHeader header,
    Ints particleSumPerStrand,
    List<String> materials,
    Shorts sourcePositions,
    Ints particleInfo,
    Ints segments,
    Shorts unknown1,
    List<HairStrandInfoGPU> gpuStrands,
    Floats particleStrandDistances,
    Shorts strandRootUVs,
    List<HairStrandBlendShape> strandBlendShapeInfo
) {
    public static HairMesh read(BinarySource source) throws IOException {
        var header = HairHeader.read(source);
        var particleSumPerStrand = source.readInts(header.numStrands());
        var materials = source.readStrings(source.readInt(), StringFormat.INT_LENGTH);
        var sourcePositions = source.readShorts(header.numParticles() * 4);
        var particleInfo = source.readInts(header.numParticles());
        var segments = source.readInts(header.numSegments());
        var unknown1 = source.readShorts(header.numParticles());

        var gpuStrands = (List<HairStrandInfoGPU>) null;
        var particleStrandDistances = (Floats) null;
        var strandRootUVs = (Shorts) null;
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
