package be.twofold.valen.core.geometry;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.util.*;

public final class Geo {
    private final boolean flipWindingOrder;

    public Geo(boolean flipWindingOrder) {
        this.flipWindingOrder = flipWindingOrder;
    }

    public Mesh readMesh(
        BinaryReader reader,
        GeoAccessor<MutableInts> indexAccessor,
        int indexCount,
        List<GeoAccessor<?>> vertexAccessors,
        int vertexCount
    ) throws IOException {
        var startPosition = reader.position();

        reader.position(startPosition);
        var indexBuffer = readIndexBuffer(reader, indexAccessor, indexCount);
        if (flipWindingOrder) {
            invertIndices((MutableInts) indexBuffer.indices());
        }

        var vertexBuffers = new ArrayList<VertexBuffer<?>>();
        for (var vertexAccessor : vertexAccessors) {
            reader.position(startPosition);
            var vertexBuffer = readVertexBuffer(reader, vertexAccessor, vertexCount);
            vertexBuffers.add(vertexBuffer);
        }

        reader.position(startPosition);

        var positions = vertexBuffers.stream()
            .filter(vb -> vb.info().semantic() == Semantic.POSITION)
            .findFirst()
            .map(vb -> (Floats) vb.buffer())
            .orElseThrow();

        var normals = vertexBuffers.stream()
            .filter(vb -> vb.info().semantic() == Semantic.NORMAL)
            .findFirst()
            .map(vb -> (Floats) vb.buffer());

        var tangents = vertexBuffers.stream()
            .filter(vb -> vb.info().semantic() == Semantic.TANGENT)
            .findFirst()
            .map(vb -> (Floats) vb.buffer());

        var texCoords = vertexBuffers.stream()
            .filter(vb -> vb.info().semantic() == Semantic.TEX_COORD)
            .map(vb -> (Floats) vb.buffer())
            .toList();

        var joints = vertexBuffers.stream()
            .filter(vb -> vb.info().semantic() == Semantic.JOINTS)
            .findFirst();

        var weights = vertexBuffers.stream()
            .filter(vb -> vb.info().semantic() == Semantic.WEIGHTS)
            .findFirst()
            .map(vb -> (Floats) vb.buffer());

        var colors = vertexBuffers.stream()
            .filter(vb -> vb.info().semantic() == Semantic.COLOR)
            .findFirst()
            .map(vb -> (Bytes) vb.buffer());

        var vertexBuffer = new VertexBuffer2(
            positions,
            normals,
            tangents,
            texCoords,
            joints.map(vb -> (Shorts) vb.buffer()),
            weights,
            colors,
            joints.map(vb -> vb.info().size()).orElse(0)
        );

        return new Mesh(indexBuffer, vertexBuffer);
    }

    private IndexBuffer readIndexBuffer(BinaryReader reader, GeoAccessor<MutableInts> accessor, int count) throws IOException {
        var capacity = count * accessor.info().size();
        var buffer = MutableInts.allocate(capacity);

        var start = reader.position() + accessor.offset();
        var offset = 0;
        for (var i = 0L; i < count; i++) {
            reader.position(start + i * accessor.stride());
            offset += accessor.reader().read(reader, buffer, offset);
        }

        return new IndexBuffer(buffer);
    }

    private <T extends WrappedArray> VertexBuffer<T> readVertexBuffer(BinaryReader reader, GeoAccessor<T> accessor, int count) throws IOException {
        var capacity = count * accessor.info().size();
        var buffer = accessor.info().componentType().allocate(capacity);

        var start = reader.position() + accessor.offset();
        var offset = 0;
        for (var i = 0L; i < count; i++) {
            reader.position(start + i * accessor.stride());
            offset += accessor.reader().read(reader, buffer, offset);
        }

        return new VertexBuffer<>(buffer, accessor.info());
    }

    private void invertIndices(MutableInts indexBuffer) {
        for (int i = 0, lim = indexBuffer.size(); i < lim; i += 3) {
            var temp = indexBuffer.getInt(i);
            indexBuffer.setInt(i, indexBuffer.getInt(i + 2));
            indexBuffer.setInt(i + 2, temp);
        }
    }
}
