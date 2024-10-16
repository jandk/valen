package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.accessor.*;
import be.twofold.valen.gltf.model.buffer.*;
import be.twofold.valen.gltf.model.mesh.*;
import be.twofold.valen.gltf.model.node.*;
import be.twofold.valen.gltf.types.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class GltfModelMapper {
    private final GltfContext context;
    private final GltfMaterialMapper materialMapper;
    private final GltfSkeletonMapper skeletonMapper;

    public GltfModelMapper(GltfContext context) {
        this.context = context;
        this.materialMapper = new GltfMaterialMapper(context);
        this.skeletonMapper = new GltfSkeletonMapper(context);
    }

    public NodeID map(Model model) throws IOException {
        var meshIDs = mapModel(model).stream()
            .map(context::addMesh)
            .toList();

        return model.skeleton() == null
            ? mapStaticModel(meshIDs)
            : mapAnimatedModel(meshIDs, model.skeleton());
    }

    private NodeID mapStaticModel(List<MeshID> meshIDs) {
        var meshNodeIDs = meshIDs.stream()
            .map(meshID -> context.addNode(NodeSchema.builder().mesh(meshID).build()))
            .toList();

        return context.addNode(
            NodeSchema.builder()
                .addAllChildren(meshNodeIDs)
                .build());
    }

    private NodeID mapAnimatedModel(List<MeshID> meshIDs, Skeleton skeleton) {
        var skinID = skeletonMapper.map(skeleton);

        var meshNodeIDs = meshIDs.stream()
            .map(meshID -> context.addNode(NodeSchema.builder().mesh(meshID).skin(skinID).build()))
            .toList();

        return context.addNode(
            NodeSchema.builder()
                .addAllChildren(meshNodeIDs)
                .build());
    }

    private List<MeshSchema> mapModel(Model model) throws IOException {
        var meshSchemas = new ArrayList<MeshSchema>();
        for (Mesh mesh : model.meshes()) {
            meshSchemas.add(mapMesh(mesh));
        }
        return meshSchemas;
    }

    private MeshSchema mapMesh(Mesh mesh) throws IOException {
        // Add the material
        var materialID = materialMapper.map(mesh.material());

        // Have to fix up the joints and weights first
        fixJointsAndWeights(mesh);

        var attributes = new JsonObject();
        for (var entry : mesh.vertexBuffers().entrySet()) {
            var semantic = mapSemantic(entry.getKey());
            var accessor = buildAccessor(entry.getValue(), entry.getKey());
            attributes.addProperty(semantic, accessor.id());
        }

        var faceAccessor = buildAccessor(mesh.faceBuffer(), null);
        var primitiveSchema = MeshPrimitiveSchema.builder()
            .attributes(attributes)
            .indices(faceAccessor)
            .material(materialID)
            .build();

        return MeshSchema.builder()
            .name(Optional.ofNullable(mesh.name()))
            .addPrimitives(primitiveSchema)
            .build();
    }

    private AccessorID buildAccessor(VertexBuffer buffer, Semantic semantic) {
        var target = semantic == null
            ? BufferViewTarget.ELEMENT_ARRAY_BUFFER
            : BufferViewTarget.ARRAY_BUFFER;

        var bufferView = context.createBufferView(buffer.buffer(), target);

        var bounds = semantic == Semantic.Position
            ? Bounds.calculate(((FloatBuffer) buffer.buffer()))
            : null;

        var accessor = AccessorSchema.builder()
            .bufferView(bufferView)
            .componentType(mapComponentType(buffer.componentType()))
            .count(buffer.count())
            .type(mapElementType(buffer.elementType()));

        if (bounds != null) {
            accessor.min(mapVector3(bounds.min()));
            accessor.max(mapVector3(bounds.max()));
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

    private AccessorComponentType mapComponentType(ComponentType<?> componentType) {
        if (componentType == ComponentType.Byte) {
            return AccessorComponentType.BYTE;
        } else if (componentType == ComponentType.UnsignedByte) {
            return AccessorComponentType.UNSIGNED_BYTE;
        } else if (componentType == ComponentType.Short) {
            return AccessorComponentType.SHORT;
        } else if (componentType == ComponentType.UnsignedShort) {
            return AccessorComponentType.UNSIGNED_SHORT;
        } else if (componentType == ComponentType.UnsignedInt) {
            return AccessorComponentType.UNSIGNED_INT;
        } else if (componentType == ComponentType.Float) {
            return AccessorComponentType.FLOAT;
        } else {
            throw new UnsupportedOperationException("Unsupported component type: " + componentType);
        }
    }

    public static AccessorType mapElementType(ElementType type) {
        return switch (type) {
            case Scalar -> AccessorType.SCALAR;
            case Vector2 -> AccessorType.VEC2;
            case Vector3 -> AccessorType.VEC3;
            case Vector4 -> AccessorType.VEC4;
            case Matrix2 -> AccessorType.MAT2;
            case Matrix3 -> AccessorType.MAT3;
            case Matrix4 -> AccessorType.MAT4;
        };
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

    private Vec3 mapVector3(Vector3 v) {
        return new Vec3(v.x(), v.y(), v.z());
    }
}
