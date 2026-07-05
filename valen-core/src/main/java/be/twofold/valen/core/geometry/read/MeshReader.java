package be.twofold.valen.core.geometry.read;

import be.twofold.valen.core.geometry.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class MeshReader {
    private final boolean flipWindingOrder;

    public MeshReader(boolean flipWindingOrder) {
        this.flipWindingOrder = flipWindingOrder;
    }

    public Mesh readMesh(
        BinarySource source,
        MeshInfo meshInfo
    ) {
        int vertexCount = meshInfo.vertexCount();

        var indices = readVertexBuffer(source, meshInfo.indices(), meshInfo.indexCount());
        if (flipWindingOrder) {
            invertIndices(indices);
        }

        var builder = Mesh.builder(indices, vertexCount);
        builder.attribute(Semantic.POSITION, readAttribute(source, meshInfo.positions(), vertexCount));
        meshInfo.normals().ifPresent(info -> builder.attribute(Semantic.NORMAL, readAttribute(source, info, vertexCount)));
        meshInfo.tangents().ifPresent(info -> builder.attribute(Semantic.TANGENT, readAttribute(source, info, vertexCount)));
        var texCoords = meshInfo.texCoords();
        for (var i = 0; i < texCoords.size(); i++) {
            builder.attribute(new Semantic.TexCoord(i), readAttribute(source, texCoords.get(i), vertexCount));
        }
        var colors = meshInfo.colors();
        for (var i = 0; i < colors.size(); i++) {
            builder.attribute(new Semantic.Color(i), readAttribute(source, colors.get(i), vertexCount));
        }
        meshInfo.joints().ifPresent(info -> builder.attribute(Semantic.JOINTS, readAttribute(source, info, vertexCount)));
        meshInfo.weights().ifPresent(info -> builder.attribute(Semantic.WEIGHTS, readAttribute(source, info, vertexCount)));
        meshInfo.custom().forEach((name, info) -> builder.attribute(new Semantic.Custom(name), readAttribute(source, info, vertexCount)));

        return builder.build();
    }

    private <T extends Slice> VertexBuffer<T> readAttribute(BinarySource source, BufferInfo<T> info, int count) {
        T buffer = readVertexBuffer(source, info, count);
        return new VertexBuffer<>(buffer, info.layout());
    }

    private <T extends Slice> T readVertexBuffer(BinarySource source, BufferInfo<T> accessor, int count) {
        int capacity = count * accessor.count();
        T buffer = accessor.allocate(capacity);

        try {
            long start = accessor.offset();
            int offset = 0;
            for (long i = 0L; i < count; i++) {
                source.position(start + i * accessor.stride());
                accessor.reader().read(source, buffer, offset);
                offset += accessor.count();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return buffer;
    }

    private void invertIndices(Ints.Mutable ints) {
        for (int i = 0, lim = ints.length(); i < lim; i += 3) {
            int temp = ints.get(i);
            ints.set(i, ints.get(i + 2));
            ints.set(i + 2, temp);
        }
    }
}
