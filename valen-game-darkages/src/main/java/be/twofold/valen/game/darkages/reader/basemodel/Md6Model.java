package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.BinaryReader;
import be.twofold.valen.game.darkages.reader.geometry.*;

import java.io.*;
import java.util.*;

public record Md6Model(
    Md6ModelHeader header,
    List<Md6ModelMeshInfo> meshInfos,
    List<Md6ModelSurfaceInfo> surfaceInfos,
    int unknown,
    List<Md6ModelWound> modelWounds,
    Md6ModelGeoDecals geoDecals,
    List<Md6ModelWoundOffset> woundOffsets,
    List<GeometryDiskLayout> diskLayouts
) {
    public static Md6Model read(BinaryReader reader, int numJoints8) throws IOException {
        var header = Md6ModelHeader.read(reader, numJoints8);
        var meshInfos = reader.readObjects(reader.readInt(), s -> Md6ModelMeshInfo.read(s, header.numLods()));
        var surfaceInfos = reader.readObjects(reader.readInt(), Md6ModelSurfaceInfo::read);
        var numModelWounds = reader.readInt();
        var numMeshWounds = reader.readInt();
        var unknown = reader.readInt();
        var modelWounds = reader.readObjects(numModelWounds, Md6ModelWound::read);
        var geoDecals = Md6ModelGeoDecals.read(reader);
        var numStreams = reader.readInt();
        var woundOffsets = reader.readObjects(numStreams, Md6ModelWoundOffset::read);
        var diskLayouts = reader.readObjects(numStreams, GeometryDiskLayout::read);

        return new Md6Model(
            header,
            meshInfos,
            surfaceInfos,
            unknown,
            modelWounds,
            geoDecals,
            woundOffsets,
            diskLayouts
        );
    }
}
