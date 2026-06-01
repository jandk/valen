package be.twofold.valen.game.doom.readers.model;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.doom.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class ModelReader implements AssetReader.Binary<Model, DoomAsset> {
    @Override
    public boolean canRead(DoomAsset asset) {
        return asset.rawType().equals("model");
    }

    @Override
    public Model read(BinarySource source, DoomAsset asset, LoadingContext context) throws IOException {
        var model = StaticModel.read(source);

        var meshes = model.surfaces().stream()
            .map(this::mapSurface)
            .toList();

        return new Model(meshes, Axis.Z);
    }

    private Mesh mapSurface(ModelSurface surface) {
        var triangles = surface.triangles();

        var indices = mapIndices(triangles.indexBuffer());

        var positions = Floats.allocate(triangles.numVerts() * 3);
        var texCoords = Floats.allocate(triangles.numVerts() * 2);
        var normals = Floats.allocate(triangles.numVerts() * 3);
        var vertexSource = BinarySource
            .wrap(triangles.vertexBuffer())
            .order(ByteOrder.BIG_ENDIAN);
        try {
            var xyzScale = triangles.xyzScale();
            var xyzBias = triangles.xyzBias();
            var stScale = triangles.stScale();
            var stBias = triangles.stBias();
            for (int i = 0; i < triangles.numVerts(); i++) {
                positions.set(i * 3 + 0, Math.fma(vertexSource.readFloat(), xyzScale.x(), xyzBias.x()) * 0.01f);
                positions.set(i * 3 + 1, Math.fma(vertexSource.readFloat(), xyzScale.y(), xyzBias.y()) * 0.01f);
                positions.set(i * 3 + 2, Math.fma(vertexSource.readFloat(), xyzScale.z(), xyzBias.z()) * 0.01f);

                texCoords.set(i * 2 + 0, Math.fma(vertexSource.readFloat(), stScale.x(), stBias.x()));
                texCoords.set(i * 2 + 1, Math.fma(vertexSource.readFloat(), stScale.y(), stBias.y()));

                var nx = MathF.unpackUNorm8Normal(vertexSource.readByte());
                var ny = MathF.unpackUNorm8Normal(vertexSource.readByte());
                var nz = MathF.unpackUNorm8Normal(vertexSource.readByte());
                normals.set(i * 3 + 0, nx);
                normals.set(i * 3 + 1, ny);
                normals.set(i * 3 + 2, nz);

                vertexSource.skip(1 + 24);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return new Mesh(indices, positions, Optional.of(normals), Optional.empty(), List.of(texCoords), List.of(), Optional.empty(), Optional.empty(), 0, Map.of());
    }

    private Ints mapIndices(Shorts indexBuffer) {
        var indices = Ints.Mutable.allocate(indexBuffer.length());
        for (int i = 0; i < indexBuffer.length(); i += 3) {
            int f0 = indexBuffer.getUnsigned(i + 0);
            int f1 = indexBuffer.getUnsigned(i + 1);
            int f2 = indexBuffer.getUnsigned(i + 2);
            indices.set(i + 0, f2);
            indices.set(i + 1, f1);
            indices.set(i + 2, f0);
        }
        return indices;
    }
}
