package be.twofold.valen.export.cast.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.format.cast.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;

public final class CastModelMapper {
    private final CastMaterialMapper materialMapper;

    public CastModelMapper(Path castPath, Path imagePath) {
        materialMapper = new CastMaterialMapper(castPath, imagePath);
    }

    public void map(Model value, CastNode.Root root) throws IOException {
        var model = root.createModel();
        value.name().ifPresent(model::setName);
        for (var mesh : value.meshes()) {
            mapMesh(model, mesh);
        }
    }

    private void mapMesh(CastNode.Model model, Mesh mesh) throws IOException {
        var meshNode = model.createMesh();
        mesh.name().ifPresent(meshNode::setName);
        meshNode.setFaceBuffer(mesh.indexBuffer().buffer());
        meshNode.setVertexPositionBuffer((FloatBuffer) mesh.getBuffer(Semantic.POSITION).orElseThrow().buffer());
        mesh.getBuffer(Semantic.NORMAL).ifPresent(buffer -> meshNode.setVertexNormalBuffer((FloatBuffer) buffer.buffer()));
        mesh.getBuffer(Semantic.TANGENT).ifPresent(buffer -> meshNode.setVertexTangentBuffer(mapTangentBuffer((FloatBuffer) buffer.buffer())));

        var colorBuffers = mesh.getBuffers(Semantic.Color.class);
        colorBuffers.forEach(buffer -> meshNode.addVertexColorBuffer(mapColorBuffer(buffer.buffer())));
        meshNode.setColorLayerCount(colorBuffers.size());

        var uvBuffers = mesh.getBuffers(Semantic.TexCoord.class);
        uvBuffers.forEach(buffer -> meshNode.addVertexUVBuffer((FloatBuffer) buffer.buffer()));
        meshNode.setUVLayerCount(uvBuffers.size());

        if (mesh.material().isPresent()) {
            meshNode.setMaterial(materialMapper.map(mesh.material().get(), model));
        }
    }

    private FloatBuffer mapTangentBuffer(FloatBuffer buffer) {
        buffer.rewind();
        var limit = buffer.limit();
        for (int i = 0, o = 0; i < limit; i += 4, o += 3) {
            buffer.put(o/**/, buffer.get(i/**/));
            buffer.put(o + 1, buffer.get(i + 1));
            buffer.put(o + 2, buffer.get(i + 2));
        }
        buffer.limit(limit * 3 / 4);
        return buffer;
    }

    private IntBuffer mapColorBuffer(Buffer buffer) {
        if (!(buffer instanceof ByteBuffer bb)) {
            throw new IllegalArgumentException("Unsupported color buffer type: " + buffer.getClass());
        }
        return bb.asIntBuffer();
    }
}
