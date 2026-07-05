package be.twofold.valen.core.geometry.read;

import be.twofold.valen.core.geometry.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class MeshReader {
    private final boolean flipWindingOrder;

    public MeshReader(boolean flipWindingOrder) {
        this.flipWindingOrder = flipWindingOrder;
    }

    public Mesh readMesh(BinarySource source, MeshFormat plan) {
        var indices = read(source, plan.indices(), plan.indexCount());
        if (flipWindingOrder) {
            invertIndices(indices);
        }

        var builder = Mesh.builder(indices, plan.vertexCount());
        plan.accessors().forEach((semantic, accessor) ->
            builder.attribute(semantic, readAttribute(source, accessor, plan.vertexCount())));
        return builder.build();
    }

    private <T extends Slice> VertexBuffer<T> readAttribute(BinarySource source, Accessor<T> accessor, int count) {
        return new VertexBuffer<>(read(source, accessor, count), accessor.layout());
    }

    private <T extends Slice> T read(BinarySource source, Accessor<T> accessor, int count) {
        int capacity = count * accessor.count();
        T buffer = accessor.allocate(capacity);

        try {
            long start = accessor.offset();
            int offset = 0;
            for (long i = 0L; i < count; i++) {
                source.position(start + i * accessor.stride());
                accessor.reader().read(source, buffer, offset);
                offset += accessor.count();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return buffer;
    }

    private void invertIndices(Ints.Mutable ints) {
        for (int i = 0, lim = ints.length(); i < lim; i += 3) {
            int temp = ints.get(i);
            ints.set(i, ints.get(i + 2));
            ints.set(i + 2, temp);
        }
    }
}
