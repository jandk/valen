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
        GeoAccessor<?> indexAccessor,
        int indexCount,
        List<GeoAccessor<?>> vertexAccessors,
        int vertexCount
    ) throws IOException {
        var startPosition = reader.position();

        reader.position(startPosition);
        var indexBuffer = readBuffer(reader, indexAccessor, indexCount);
        if (flipWindingOrder) {
            invertIndices(indexBuffer.buffer());
        }

        var vertexBuffers = new ArrayList<VertexBuffer<?>>();
        for (var vertexAccessor : vertexAccessors) {
            reader.position(startPosition);
            var vertexBuffer = readBuffer(reader, vertexAccessor, vertexCount);
            vertexBuffers.add(vertexBuffer);
        }

        reader.position(startPosition);
        return new Mesh(indexBuffer, vertexBuffers);
    }

    private <T extends WrappedArray> VertexBuffer<T> readBuffer(BinaryReader reader, GeoAccessor<T> accessor, int count) throws IOException {
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

    private void invertIndices(Object array) {
        switch (array) {
            case MutableBytes bytes -> invert(bytes);
            case MutableShorts shorts -> invert(shorts);
            case MutableInts ints -> invert(ints);
            default -> throw new UnsupportedOperationException("Unsupported array type: " + array.getClass());
        }
    }

    private void invert(MutableBytes bytes) {
        for (int i = 0, lim = bytes.size(); i < lim; i += 3) {
            var temp = bytes.getByte(i);
            bytes.setByte(i, bytes.getByte(i + 2));
            bytes.setByte(i + 2, temp);
        }
    }

    private void invert(MutableShorts shorts) {
        for (int i = 0, lim = shorts.size(); i < lim; i += 3) {
            var temp = shorts.getShort(i);
            shorts.setShort(i, shorts.getShort(i + 2));
            shorts.setShort(i + 2, temp);
        }
    }

    private void invert(MutableInts ints) {
        for (int i = 0, lim = ints.size(); i < lim; i += 3) {
            var temp = ints.getInt(i);
            ints.setInt(i, ints.getInt(i + 2));
            ints.setInt(i + 2, temp);
        }
    }
}
