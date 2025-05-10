package be.twofold.valen.export.cast;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.format.cast.io.*;
import be.twofold.valen.format.cast.model.*;

import java.io.*;
import java.nio.*;

public final class CastModelExporter extends CastExporter<Model> {
    @Override
    public String getID() {
        return "model.cast";
    }

    @Override
    public Class<Model> getSupportedType() {
        return Model.class;
    }

    @Override
    public void export(Model value, OutputStream out) throws IOException {
        var model = ImmutableModel.builder()
            .name(value.name())
            .addAllMeshes(value.meshes().stream()
                .map(this::mapMesh)
                .toList())
            .build();

        var writer = new CastWriter(out);
        writer.write(model);
    }

    private MeshNode mapMesh(Mesh mesh) {
        return ImmutableMesh.builder()
            .name(mesh.name())
            .faceBuffer(mesh.indexBuffer().buffer())
            .vertexPositionBuffer((FloatBuffer) mesh.getBuffer(Semantic.POSITION).orElseThrow().buffer())
            .build();
    }
}
