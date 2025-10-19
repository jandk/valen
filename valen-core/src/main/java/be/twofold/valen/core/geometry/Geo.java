package be.twofold.valen.core.geometry;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class Geo {
    private final boolean flipWindingOrder;

    public Geo(boolean flipWindingOrder) {
        this.flipWindingOrder = flipWindingOrder;
    }

    public Mesh readMesh(
        BinaryReader reader,
        GeoMeshInfo meshInfo
    ) {
        var indices = readVertexBuffer(reader, meshInfo.indices(), meshInfo.indexCount());
        if (flipWindingOrder) {
            invertIndices(indices);
        }

        Floats positions = readVertexBuffer(reader, meshInfo.positions(), meshInfo.vertexCount());
        Optional<Floats> normals = meshInfo.normals().map(info -> readVertexBuffer(reader, info, meshInfo.vertexCount()));
        Optional<Floats> tangents = meshInfo.tangents().map(info -> readVertexBuffer(reader, info, meshInfo.vertexCount()));
        List<Floats> texCoords = meshInfo.texCoords().stream()
            .map(info -> readVertexBuffer(reader, info, meshInfo.vertexCount()))
            .collect(Collectors.toUnmodifiableList());
        List<Bytes> colors = meshInfo.colors().stream()
            .map(info -> readVertexBuffer(reader, info, meshInfo.vertexCount()))
            .collect(Collectors.toUnmodifiableList());
        Optional<Shorts> joints = meshInfo.joints().map(info -> readVertexBuffer(reader, info, meshInfo.vertexCount()));
        Optional<Floats> weights = meshInfo.weights().map(info -> readVertexBuffer(reader, info, meshInfo.vertexCount()));
        Map<String, VertexBuffer<?>> custom = new HashMap<>();
        for (var entry : meshInfo.custom().entrySet()) {
            var bufferInfo = entry.getValue();
            var buffer = readVertexBuffer(reader, bufferInfo, meshInfo.vertexCount());
            var vertexBuffer = new VertexBuffer<>(buffer, bufferInfo);
            custom.put(entry.getKey(), vertexBuffer);
        }

        return new Mesh(indices, positions, normals, tangents, texCoords, colors, joints, weights, 0, custom);
    }

    private <T extends WrappedArray> T readVertexBuffer(BinaryReader reader, GeoBufferInfo<T> accessor, int count) {
        int capacity = count * accessor.count();
        T buffer = accessor.allocate(capacity);

        try {
            long start = accessor.offset();
            int offset = 0;
            for (long i = 0L; i < count; i++) {
                reader.position(start + i * accessor.stride());
                accessor.reader().read(reader, buffer, offset);
                offset += accessor.count();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return buffer;
    }

    private void invertIndices(MutableInts ints) {
        for (int i = 0, lim = ints.size(); i < lim; i += 3) {
            int temp = ints.getInt(i);
            ints.setInt(i, ints.getInt(i + 2));
            ints.setInt(i + 2, temp);
        }
    }
}
