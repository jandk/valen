package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.export.gltf.*;
import be.twofold.valen.export.gltf.model.*;
import com.google.gson.*;

import java.nio.*;
import java.util.*;
import java.util.stream.*;

public final class GltfModelMapper {
    private final GltfContext context;
    private final GltfMaterialMapper materialMapper;

    public GltfModelMapper(GltfContext context) {
        this.context = context;
        this.materialMapper = new GltfMaterialMapper(context);
    }

    public MeshSchema map(Model model) {
        // First we do the meshes
        var primitives = model.meshes().stream()
            .map(mesh -> this.mapMesh(mesh, model.materials()))
            .toList();

        return MeshSchema.builder()
            .primitives(primitives)
            .build();
    }

    private MeshPrimitiveSchema mapMesh(Mesh mesh, List<Material> materials) {
        // Have to fix up the joints and weights first
        fixJointsAndWeights(mesh);
        Material material = materials.get(mesh.materialIndex());
        var materialId = context.findMaterial(material.name());
        if (materialId.getId() == -1) {
            System.out.println("Mapping " + material.name());
            materialId = context.addMaterial(materialMapper.map(material));
        }

        var attributes = new JsonObject();
        for (var entry : mesh.vertexBuffers().entrySet()) {
            if (entry.getKey().equals(Semantic.Color0)) {
                continue;
            }
            var semantic = mapSemantic(entry.getKey());
            var accessor = buildAccessor(entry.getValue(), entry.getKey());
            attributes.addProperty(semantic, accessor.getId());
        }
        assert IntStream.range(0, mesh.faceBuffer().count()).map(operand -> ((ShortBuffer) mesh.faceBuffer().buffer()).get(operand)).max().getAsInt() < mesh.vertexBuffers().get(Semantic.Position).count();
        var faceAccessor = buildAccessor(mesh.faceBuffer(), null);
        return MeshPrimitiveSchema.builder()
            .attributes(attributes)
            .indices(faceAccessor)
            .material(materialId)
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
        // TODO: Loop over joints and weights and fix them
        mesh.getBuffer(Semantic.Joints0).ifPresent(joints -> mesh
            .getBuffer(Semantic.Weights0).ifPresent(weights -> {
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
            case Semantic.Position() -> "POSITION";
            case Semantic.Normal() -> "NORMAL";
            case Semantic.Tangent() -> "TANGENT";
            case Semantic.TexCoord(var n) -> "TEXCOORD_" + n;
            case Semantic.Color(var n) -> "COLOR_" + n;
            case Semantic.Joints(var n) -> "JOINTS_" + n;
            case Semantic.Weights(var n) -> "WEIGHTS_" + n;
        };
    }
}
