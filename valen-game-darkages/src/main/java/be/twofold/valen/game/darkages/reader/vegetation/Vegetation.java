package be.twofold.valen.game.darkages.reader.vegetation;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.darkages.reader.geometry.*;

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
    public static Vegetation read(DataSource source) throws IOException {
        var header = VegetationHeader.read(source);
        if (header.numSurfaces() <= 0) {
            throw new UnsupportedOperationException();
        }
        var surfaces = source.readObjects(header.numSurfaces(), ds -> VegetationSurface.read(ds, header.numLods()));
        var windData = VegetationWindData.read(source);
        var hasBillBoards = source.readBoolByte();
        var maxLodDistance = source.readFloat();
        var unknown = source.readFloat();
        var hasCustomNormals = source.readBoolByte();
        var layouts = source.readObjects(header.numLods(), GeometryDiskLayout::read);
        var collisionData = source.readObjects(source.readInt(), VegetationCollisionData::read);

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
