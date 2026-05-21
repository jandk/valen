package be.twofold.valen.game.darkages.reader.strandshair;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

record StrandsHair(
    StrandsHairHeader header,
    Ints strands,
    Shorts particles,
    Floats distances,
    Ints infos,
    Ints segments,
    Shorts visibility,
    List<StrandsHairInfo> strandsInfo,
    List<StrandsHairLocationInfo> locationInfo,
    boolean hasCollisionGrid,
    String collisionGridName,
    String attachedMeshName
) {
    static StrandsHair read(BinarySource source) throws IOException {
        var header = StrandsHairHeader.read(source);
        var strands = source.readInts(header.numStrands());
        var particles = source.readShorts(header.numParticles() * 4);
        var distances = source.readFloats(header.numStrands());
        var infos = source.readInts(header.numParticles());
        var segments = source.readInts(header.numSegments());
        var visibility = source.readShorts(header.numParticles() / 4 * 4);
        var strandsInfo = source.readObjects(header.numStrands(), StrandsHairInfo::read);
        var locationInfo = source.readObjects(
            header.numBlendShapeLODs() * header.numStrands(),
            StrandsHairLocationInfo::read
        );
        var hasCollisionGrid = source.readBool(BoolFormat.BYTE);
        var collisionGridName = source.readString(StringFormat.INT_LENGTH);
        var attachedMeshName = source.readString(StringFormat.INT_LENGTH);

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
