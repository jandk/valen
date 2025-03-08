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
        GeoAccessor<?> indexAccessor,
        List<GeoAccessor<?>> vertexAccessors
    ) throws IOException {
        var startPosition = source.position();

        source.position(startPosition);
        var indexBuffer = readBuffer(source, indexAccessor);
        if (flipWindingOrder) {
            invertIndices(indexBuffer.buffer());
        }

        var vertexBuffers = new ArrayList<VertexBuffer<?>>();
        for (var accessor : vertexAccessors) {
            source.position(startPosition);
            var vertexBuffer = readBuffer(source, accessor);
            vertexBuffers.add(vertexBuffer);
        }

        source.position(startPosition);
        return new Mesh(indexBuffer, vertexBuffers);
    }

    static <T extends Buffer> VertexBuffer<T> readBuffer(DataSource source, GeoAccessor<T> accessor) throws IOException {
        var capacity = accessor.count() * accessor.info().elementType().size();
        var buffer = accessor.info().componentType().allocate(capacity);

        var start = source.position() + accessor.offset();
        for (var i = 0L; i < accessor.count(); i++) {
            source.position(start + i * accessor.stride());
            accessor.reader().read(source, buffer);
        }

        buffer.flip();
        return new VertexBuffer<>(buffer, accessor.info());
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

}
