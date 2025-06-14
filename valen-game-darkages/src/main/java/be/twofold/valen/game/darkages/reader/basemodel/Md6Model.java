package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.*;
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
    public static Md6Model read(DataSource source, int numJoints8) throws IOException {
        var header = Md6ModelHeader.read(source, numJoints8);
        var meshInfos = source.readObjects(source.readInt(), s -> Md6ModelMeshInfo.read(s, header.numLods()));
        var surfaceInfos = source.readObjects(source.readInt(), Md6ModelSurfaceInfo::read);
        var numModelWounds = source.readInt();
        var numMeshWounds = source.readInt();
        var unknown = source.readInt();
        var modelWounds = source.readObjects(numModelWounds, Md6ModelWound::read);
        var geoDecals = Md6ModelGeoDecals.read(source);
        var numStreams = source.readInt();
        var woundOffsets = source.readObjects(numStreams, Md6ModelWoundOffset::read);
        var diskLayouts = source.readObjects(numStreams, GeometryDiskLayout::read);

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
