package be.twofold.valen.core.geometry;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class Geo {
    private final boolean flipWindingOrder;

    public Geo(boolean flipWindingOrder) {
        this.flipWindingOrder = flipWindingOrder;
    }

    public Mesh readMesh(
        DataSource source,
        List<Accessor<?>> vertexAccessors,
        Accessor<?> indexAccessor
    ) throws IOException {
        var startPos = source.position();

        var vertexBuffers = new HashMap<Semantic, VertexBuffer>();
        for (var accessor : vertexAccessors) {
            source.position(startPos);
            var vertexBuffer = accessor.read(source);
            vertexBuffers.put(accessor.info().semantic(), vertexBuffer);
        }

        source.position(startPos);
        var indexBuffer = indexAccessor.read(source);
        if (flipWindingOrder) {
            invertIndices(indexBuffer.buffer());
        }

        source.position(startPos);
        return new Mesh(indexBuffer, vertexBuffers);
    }

    private void invertIndices(Buffer buffer) {
        switch (buffer) {
            case ByteBuffer bb -> invert(bb);
            case ShortBuffer sb -> invert(sb);
            case IntBuffer ib -> invert(ib);
            default -> throw new UnsupportedOperationException("Unsupported buffer type: " + buffer.getClass());
        }
    }

    private void invert(ByteBuffer bb) {
        for (int i = 0, lim = bb.limit(); i < lim; i += 3) {
            var temp = bb.get(i);
            bb.put(i, bb.get(i + 2));
            bb.put(i + 2, temp);
        }
    }

    private void invert(ShortBuffer sb) {
        for (int i = 0, lim = sb.limit(); i < lim; i += 3) {
            var temp = sb.get(i);
            sb.put(i, sb.get(i + 2));
            sb.put(i + 2, temp);
        }
    }

    private void invert(IntBuffer ib) {
        for (int i = 0, lim = ib.limit(); i < lim; i += 3) {
            var temp = ib.get(i);
            ib.put(i, ib.get(i + 2));
            ib.put(i + 2, temp);
        }
    }

    @FunctionalInterface
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
        public VertexBuffer read(DataSource source) throws IOException {
            var numPrimitives = count * info.elementType().size();
            var buffer = info.componentType().allocate(numPrimitives);

            var start = source.position() + offset;
            for (var i = 0L; i < count; i++) {
                source.position(start + i * stride);
                reader.read(source, buffer);
            }

            buffer.flip();
            return new VertexBuffer(buffer, info);
        }
    }
}
