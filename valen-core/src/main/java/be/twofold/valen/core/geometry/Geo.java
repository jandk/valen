package be.twofold.valen.core.geometry;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class Geo {

    public static Mesh readMesh(DataSource source, List<Accessor> vertexAccessors, Accessor faceAccessor) throws IOException {
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
        var faceBuffer = new VertexBuffer(buffer, faceAccessor.info());

        source.seek(startPos);
        return new Mesh(faceBuffer, vertexBuffers, -1);
    }

    public static Buffer read(
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

    public record Accessor(
        int offset,
        int count,
        int stride,
        VertexBuffer.Info info,
        Reader reader
    ) {
    }

    public record AccessorInfo(
        Semantic semantic,
        ElementType elementType,
        ComponentType componentType,
        boolean normalized,
        Reader reader
    ) {
    }

    public interface Reader {
        void read(DataSource source, Buffer buffer) throws IOException;
    }

}
