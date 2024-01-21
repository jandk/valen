package be.twofold.valen.export.gltf;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.export.gltf.model.*;
import com.google.gson.*;

import java.nio.*;

final class GltfModelMapper {
    private final GltfContext context;

    GltfModelMapper(GltfContext context) {
        this.context = context;
    }

    MeshSchema map(Model model) {
        // First we do the meshes
        var primitives = model.meshes().stream()
            .map(this::mapMesh)
            .toList();

        return new MeshSchema(primitives);
    }

    private PrimitiveSchema mapMesh(Mesh mesh) {
        var attributes = new JsonObject();

        // Have to fix up the joints and weights first
        fixJointsAndWeights(mesh);

        for (var entry : mesh.vertexBuffers().entrySet()) {
            var semantic = mapSemantic(entry.getKey());
            var accessor = buildAccessor(entry.getValue(), entry.getKey());
            attributes.addProperty(semantic, accessor);
        }

        int faceAccessor = buildAccessor(mesh.faceBuffer(), null);
        return new PrimitiveSchema(
            attributes,
            faceAccessor
        );
    }

    private int buildAccessor(VertexBuffer buffer, Semantic semantic) {
        var target = semantic == null
            ? BufferViewTarget.ELEMENT_ARRAY_BUFFER
            : BufferViewTarget.ARRAY_BUFFER;

        int length = buffer.buffer().limit() * buffer.componentType().size();
        int bufferView = context.createBufferView(buffer.buffer(), length, target);

        var bounds = semantic == Semantic.Position
            ? Bounds.calculate(((FloatBuffer) buffer.buffer()))
            : null;

        var accessor = new AccessorSchema(
            bufferView,
            AccessorComponentType.from(buffer.componentType()),
            buffer.count(),
            AccessorType.from(buffer.elementType()),
            bounds != null ? bounds.min().toArray() : null,
            bounds != null ? bounds.max().toArray() : null,
            buffer.normalized() ? true : null
        );
        return context.addAccessor(accessor);
    }

    private void fixJointsAndWeights(Mesh mesh) {
        mesh.getBuffer(Semantic.Joints).ifPresent(joints -> mesh
            .getBuffer(Semantic.Weights).ifPresent(weights -> {
                var ja = ((ByteBuffer) joints.buffer()).array();
                var wa = ((ByteBuffer) weights.buffer()).array();
                for (var i = 0; i < ja.length; i++) {
                    if (wa[i] == 0) {
                        ja[i] = 0;
                    }
                }
            }));
    }

    private String mapSemantic(Semantic semantic) {
        return switch (semantic) {
            case Position -> "POSITION";
            case Normal -> "NORMAL";
            case Tangent -> "TANGENT";
            case TexCoord -> "TEXCOORD_0";
            case Color -> "COLOR_0";
            case Joints -> "JOINTS_0";
            case Weights -> "WEIGHTS_0";
        };
    }
}
