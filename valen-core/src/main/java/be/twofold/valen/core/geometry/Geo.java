package be.twofold.valen.core.geometry;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class Geo {
    private final boolean invertFaces;

    public Geo(boolean invertFaces) {
        this.invertFaces = invertFaces;
    }

    public Mesh readMesh(DataSource source, List<Accessor<?>> vertexAccessors, Accessor<?> faceAccessor) throws IOException {
        var startPos = source.tell();

        var vertexBuffers = new HashMap<Semantic, VertexBuffer>();
        for (var accessor : vertexAccessors) {
            source.seek(startPos);
            var buffer = accessor.read(source);
            var vertexBuffer = new VertexBuffer(buffer, accessor.info());
            vertexBuffers.put(accessor.info().semantic(), vertexBuffer);
        }

        source.seek(startPos);
        var buffer = faceAccessor.read(source);
        if (invertFaces) {
            invertFaces(buffer);
        }
        var faceBuffer = new VertexBuffer(buffer, faceAccessor.info());

        source.seek(startPos);
        return new Mesh(faceBuffer, vertexBuffers, -1);
    }

    private void invertFaces(Buffer buffer) {
        switch (buffer) {
            case ByteBuffer bb -> {
                for (int i = 0, lim = bb.limit(); i < lim; i += 3) {
                    var temp = bb.get(i);
                    bb.put(i, bb.get(i + 2));
                    bb.put(i + 2, temp);
                }
            }
            case ShortBuffer sb -> {
                for (int i = 0, lim = sb.limit(); i < lim; i += 3) {
                    var temp = sb.get(i);
                    sb.put(i, sb.get(i + 2));
                    sb.put(i + 2, temp);
                }
            }
            case IntBuffer ib -> {
                for (int i = 0, lim = ib.limit(); i < lim; i += 3) {
                    var temp = ib.get(i);
                    ib.put(i, ib.get(i + 2));
                    ib.put(i + 2, temp);
                }
            }
            default -> throw new UnsupportedOperationException("Unsupported buffer type: " + buffer.getClass());
        }
    }

    public interface Reader<T extends Buffer> {
        void read(DataSource source, T buffer) throws IOException;
    }

    public record Accessor<T extends Buffer>(
        int offset,
        int count,
        int stride,
        VertexBuffer.Info<T> info,
        Reader<T> reader
    ) {
        @SuppressWarnings("unchecked")
        public T read(DataSource source) throws IOException {
            var numPrimitives = count * info.elementType().size();
            var buffer = info.componentType().allocate(numPrimitives);

            var start = source.tell() + offset;
            for (var i = 0L; i < count; i++) {
                source.seek(start + i * stride);
                reader.read(source, buffer);
            }

            return (T) buffer.flip();
        }
    }
}
