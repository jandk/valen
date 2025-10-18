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
            invertIndices(indexBuffer);
        }

        var vertexBuffers = new ArrayList<VertexBuffer<?>>();
        for (var vertexAccessor : vertexAccessors) {
            reader.position(startPosition);
            var vertexBuffer = readVertexBuffer(reader, vertexAccessor, vertexCount);
            vertexBuffers.add(vertexBuffer);
        }

        reader.position(startPosition);
        return new Mesh(indexBuffer, vertexBuffers);
    }

    private MutableInts readIndexBuffer(BinaryReader reader, GeoAccessor<MutableInts> accessor, int count) throws IOException {
        var capacity = count * accessor.info().size();
        var buffer = MutableInts.allocate(capacity);

        var start = reader.position() + accessor.offset();
        var offset = 0;
        for (var i = 0L; i < count; i++) {
            reader.position(start + i * accessor.stride());
            offset += accessor.reader().read(reader, buffer, offset);
        }

        return buffer;
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
