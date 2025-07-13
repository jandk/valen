package be.twofold.valen.game.darkages.reader.strandshair;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

record StrandsHair(
    StrandsHairHeader header,
    int[] strands,
    short[] particles,
    float[] distances,
    int[] infos,
    int[] segments,
    short[] visibility,
    List<StrandsHairInfo> strandsInfo,
    List<StrandsHairLocationInfo> locationInfo,
    boolean hasCollisionGrid,
    String collisionGridName,
    String attachedMeshName
) {
    static StrandsHair read(BinaryReader reader) throws IOException {
        var header = StrandsHairHeader.read(reader);
        var strands = reader.readInts(header.numStrands());
        var particles = reader.readShorts(header.numParticles() * 4);
        var distances = reader.readFloats(header.numStrands());
        var infos = reader.readInts(header.numParticles());
        var segments = reader.readInts(header.numSegments());
        var visibility = reader.readShorts(header.numParticles() / 4 * 4);
        var strandsInfo = reader.readObjects(header.numStrands(), StrandsHairInfo::read);
        var locationInfo = reader.readObjects(
            header.numBlendShapeLODs() * header.numStrands(),
            StrandsHairLocationInfo::read
        );
        var hasCollisionGrid = reader.readBoolByte();
        var collisionGridName = reader.readPString();
        var attachedMeshName = reader.readPString();

        return new StrandsHair(
            header,
            strands,
            particles,
            distances,
            infos,
            segments,
            visibility,
            strandsInfo,
            locationInfo,
            hasCollisionGrid,
            collisionGridName,
            attachedMeshName
        );
    }
}
