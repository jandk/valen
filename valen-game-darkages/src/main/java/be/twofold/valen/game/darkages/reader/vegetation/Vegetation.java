package be.twofold.valen.game.darkages.reader.vegetation;

import be.twofold.valen.game.darkages.reader.geometry.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Vegetation(
    VegetationHeader header,
    List<VegetationSurface> surfaces,
    VegetationWindData windData,
    boolean hasBillBoards,
    float maxLodDistance,
    float unknown,
    boolean hasCustomNormals,
    List<GeometryDiskLayout> layouts,
    List<VegetationCollisionData> collisionData
) {
    public static Vegetation read(BinaryReader reader) throws IOException {
        var header = VegetationHeader.read(reader);
        if (header.numSurfaces() <= 0) {
            throw new UnsupportedOperationException();
        }
        var surfaces = reader.readObjects(header.numSurfaces(), ds -> VegetationSurface.read(ds, header.numLods()));
        var windData = VegetationWindData.read(reader);
        var hasBillBoards = reader.readBool(BoolFormat.BYTE);
        var maxLodDistance = reader.readFloat();
        var unknown = reader.readFloat();
        var hasCustomNormals = reader.readBool(BoolFormat.BYTE);
        var layouts = reader.readObjects(header.numLods(), GeometryDiskLayout::read);
        var collisionData = reader.readObjects(reader.readInt(), VegetationCollisionData::read);

        return new Vegetation(
            header,
            surfaces,
            windData,
            hasBillBoards,
            maxLodDistance,
            unknown,
            hasCustomNormals,
            layouts,
            collisionData
        );
    }
}
