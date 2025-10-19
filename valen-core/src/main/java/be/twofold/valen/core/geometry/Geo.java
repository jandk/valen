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
        GeoMeshInfo bufferInfo
    ) {
        var indices = readVertexBuffer(reader, bufferInfo.indices(), bufferInfo.indexCount());
        if (flipWindingOrder) {
            invertIndices(indices);
        }

        Floats positions = readVertexBuffer(reader, bufferInfo.positions(), bufferInfo.vertexCount());
        Optional<Floats> normals = bufferInfo.normals().map(info -> readVertexBuffer(reader, info, bufferInfo.vertexCount()));
        Optional<Floats> tangents = bufferInfo.tangents().map(info -> readVertexBuffer(reader, info, bufferInfo.vertexCount()));
        List<Floats> texCoords = bufferInfo.texCoords().stream()
            .map(info -> readVertexBuffer(reader, info, bufferInfo.vertexCount()))
            .collect(Collectors.toUnmodifiableList());
        List<Bytes> colors = bufferInfo.colors().stream()
            .map(info -> readVertexBuffer(reader, info, bufferInfo.vertexCount()))
            .collect(Collectors.toUnmodifiableList());
        Optional<Shorts> joints = bufferInfo.joints().map(info -> readVertexBuffer(reader, info, bufferInfo.vertexCount()));
        Optional<Floats> weights = bufferInfo.weights().map(info -> readVertexBuffer(reader, info, bufferInfo.vertexCount()));


        return new Mesh(
            indices,
            positions,
            normals,
            tangents,
            texCoords,
            colors,
            joints,
            weights,
            0
        );
    }

    private <T extends WrappedArray> T readVertexBuffer(BinaryReader reader, GeoBufferInfo<T> accessor, int count) {
        int capacity = count * accessor.length();
        T buffer = accessor.allocate(capacity);

        try {
            long start = accessor.offset();
            int offset = 0;
            for (long i = 0L; i < count; i++) {
                reader.position(start + i * accessor.stride());
                accessor.reader().read(reader, buffer, offset);
                offset += accessor.length();
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
