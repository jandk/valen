package be.twofold.valen.core.geometry;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class Geo {
    private final boolean invertFaces;

    public Geo(boolean invertFaces) {
        this.invertFaces = invertFaces;
    }

    public Mesh readMesh(DataSource source, List<Accessor> vertexAccessors, Accessor faceAccessor) throws IOException {
        var startPos = source.tell();

        var vertexBuffers = new HashMap<Semantic, VertexBuffer>();
        for (var accessor : vertexAccessors) {
            source.seek(startPos);
            var buffer = read(source, accessor);
            var vertexBuffer = new VertexBuffer(buffer, accessor.info());
            vertexBuffers.put(accessor.info().semantic(), vertexBuffer);
        }

        source.seek(startPos);
        var buffer = read(source, faceAccessor);
        if (invertFaces) {
            invertFaces((ShortBuffer) buffer);
        }
        var faceBuffer = new VertexBuffer(buffer, faceAccessor.info());

        source.seek(startPos);
        return new Mesh(faceBuffer, vertexBuffers, -1);
    }

    private Buffer read(
        DataSource source,
        Accessor accessor
    ) throws IOException {
        var numPrimitives = accessor.count() * accessor.info().elementType().size();
        var buffer = Buffers.allocate(numPrimitives, accessor.info().componentType());

        var start = source.tell() + accessor.offset();
        for (var i = 0L; i < accessor.count(); i++) {
            source.seek(start + i * accessor.stride());
            accessor.reader().read(source, buffer);
        }
        return buffer.flip();
    }

    private void invertFaces(ShortBuffer buffer) {
        for (int i = 0, lim = buffer.capacity(); i < lim; i += 3) {
            var temp = buffer.get(i);
            buffer.put(i, buffer.get(i + 2));
            buffer.put(i + 2, temp);
        }
    }

    public interface Reader {
        void read(DataSource source, Buffer buffer) throws IOException;
    }

    public record Accessor(
        int offset,
        int count,
        int stride,
        VertexBuffer.Info info,
        Reader reader
    ) {
    }
}
