package be.twofold.valen.game.doom.readers.staticmodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.geometry.read.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.doom.*;
import be.twofold.valen.game.doom.readers.common.*;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.nio.*;

public final class StaticModelReader implements AssetReader.Binary<Model, DoomAsset> {
    private static final int POSITION_OFFSET = 0;
    private static final int TEX_COORD_OFFSET = 12;
    private static final int NORMAL_OFFSET = 20;

    private static final float UNIT_SCALE = 0.01f;

    private final MeshReader meshReader = new MeshReader(true);

    @Override
    public boolean canRead(DoomAsset asset) {
        return asset.id().type() == ResourceType.of("model");
    }

    @Override
    public Model read(BinaryReader reader, DoomAsset asset, LoadingContext context) throws IOException {
        var model = StaticModel.read(reader);

        var meshes = model.modelSurfaces().stream()
            .map(this::mapSurface)
            .toList();

        return new Model(meshes, Axis.Z);
    }

    private Mesh mapSurface(StaticModelSurface surface) {
        var triangles = surface.geometry();

        var format = MeshFormat.builder(triangles.numIndices(), triangles.numVerts())
            .positions(POSITION_OFFSET, Triangles.VERTEX_SIZE, readPosition(triangles.xyzScale(), triangles.xyzBias()))
            .texCoords(0, TEX_COORD_OFFSET, Triangles.VERTEX_SIZE, readTexCoord(triangles.stScale(), triangles.stBias()))
            .normals(NORMAL_OFFSET, Triangles.VERTEX_SIZE, readNormal())
            .indices(triangles.numVerts() * Triangles.VERTEX_SIZE, Short.BYTES, AttributeReader.readShortAsInts())
            .build();

        var reader = BinaryReader
            .wrap(triangles.buffer())
            .order(ByteOrder.BIG_ENDIAN);

        return meshReader.readMesh(reader, format);
    }

    private static AttributeReader<Floats.Mutable> readPosition(Vector3 scale, Vector3 bias) {
        return (reader, target, offset) -> Vector3.read(reader)
            .fma(scale, bias)
            .multiply(UNIT_SCALE)
            .toSlice(target, offset);
    }

    private static AttributeReader<Floats.Mutable> readTexCoord(Vector2 scale, Vector2 bias) {
        return (reader, target, offset) -> Vector2.read(reader)
            .fma(scale, bias)
            .toSlice(target, offset);
    }

    private static AttributeReader<Floats.Mutable> readNormal() {
        return (reader, target, offset) -> {
            target.set(offset/**/, MathF.unpackUNorm8Normal(reader.readByte()));
            target.set(offset + 1, MathF.unpackUNorm8Normal(reader.readByte()));
            target.set(offset + 2, MathF.unpackUNorm8Normal(reader.readByte()));
        };
    }
}
