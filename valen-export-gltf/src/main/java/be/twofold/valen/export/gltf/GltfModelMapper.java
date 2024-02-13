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

        return MeshSchema.builder()
            .primitives(primitives)
            .build();
    }

    private MeshPrimitiveSchema mapMesh(Mesh mesh) {
        // Have to fix up the joints and weights first
        fixJointsAndWeights(mesh);

        var attributes = new JsonObject();
        for (var entry : mesh.vertexBuffers().entrySet()) {
            var semantic = mapSemantic(entry.getKey());
            var accessor = buildAccessor(entry.getValue(), entry.getKey());
            attributes.addProperty(semantic, accessor.getId());
        }

        var faceAccessor = buildAccessor(mesh.faceBuffer(), null);
        return MeshPrimitiveSchema.builder()
            .attributes(attributes)
            .indices(faceAccessor)
            .build();
    }

    private AccessorId buildAccessor(VertexBuffer buffer, Semantic semantic) {
        var target = semantic == null
            ? BufferViewTarget.ELEMENT_ARRAY_BUFFER
            : BufferViewTarget.ARRAY_BUFFER;

        var length = buffer.buffer().limit() * buffer.componentType().size();
        var bufferView = context.createBufferView(buffer.buffer(), length, target);

        var bounds = semantic == Semantic.Position
            ? Bounds.calculate(((FloatBuffer) buffer.buffer()))
            : null;

        var accessor = AccessorSchema.builder()
            .bufferView(bufferView)
            .componentType(AccessorComponentType.from(buffer.componentType()))
            .count(buffer.count())
            .type(AccessorType.from(buffer.elementType()));

        if (bounds != null) {
            accessor.min(bounds.min().toArray());
            accessor.max(bounds.max().toArray());
        }
        if (buffer.normalized()) {
            accessor.normalized(true);
        }

        return context.addAccessor(accessor.build());
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
