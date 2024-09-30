package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.accessor.*;
import be.twofold.valen.gltf.model.buffer.*;
import be.twofold.valen.gltf.model.material.*;
import be.twofold.valen.gltf.model.mesh.*;
import be.twofold.valen.gltf.model.node.*;
import be.twofold.valen.gltf.model.skin.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class GltfModelMapper {
    // TODO: Make this configurable
    private static final Quaternion ROTATION = Quaternion.fromAxisAngle(Vector3.UnitX, -MathF.HALF_PI);

    private final GltfContext context;
    private final GltfMaterialMapper materialMapper;

    public GltfModelMapper(GltfContext context) {
        this.context = context;
        this.materialMapper = new GltfMaterialMapper(context);
    }

    public NodeSchema map(Model model) throws IOException {
        var materialIDs = new ArrayList<MaterialID>();
        for (var material : model.materials()) {
            materialIDs.add(context.addMaterial(materialMapper.map(material)));
        }

        var skeletonMapper = new GltfSkeletonMapper(context, ROTATION);

        SkinID skinId = null;
        if (model.skeleton() != null) {
            skinId = context.addSkin(
                skeletonMapper.map(model.skeleton(), model.name()));
        }

        var children = new ArrayList<NodeID>();
        for (SubModel subModel : model.subModels()) {
            var primitives = subModel.meshes().stream()
                .map(mesh -> mapMesh(mesh, materialIDs.get(mesh.materialIndex())))
                .toList();

            var meshSchema = MeshSchema.builder()
                .name(subModel.name())
                .primitives(primitives)
                .build();

            var meshNode = NodeSchema.builder()
                .name(subModel.name())
                .mesh(context.addMesh(meshSchema))
                .skin(Optional.ofNullable(skinId))
                .build();
            children.add(context.addNode(meshNode));
        }
        return NodeSchema.builder()
            .name(model.name())
            .addAllChildren(children)
            .build();
    }

    private MeshPrimitiveSchema mapMesh(Mesh mesh, MaterialID materialID) {
        // Have to fix up the joints and weights first
        fixJointsAndWeights(mesh);

        var attributes = new JsonObject();
        for (var entry : mesh.vertexBuffers().entrySet()) {
            var semantic = mapSemantic(entry.getKey());
            var accessor = buildAccessor(entry.getValue(), entry.getKey());
            attributes.addProperty(semantic, accessor.id());
        }

        var faceAccessor = buildAccessor(mesh.faceBuffer(), null);
        return MeshPrimitiveSchema.builder()
            .attributes(attributes)
            .indices(faceAccessor)
            .material(materialID)
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
                var wa = ((ByteBuffer) weights.buffer()).array();
                Buffer buffer = joints.buffer();
                switch (buffer) {
                    case ByteBuffer byteBuffer -> {
                        var ja = byteBuffer.array();
                        for (var i = 0; i < ja.length; i++) {
                            if (wa[i] == 0) {
                                ja[i] = 0;
                            }
                        }
                    }
                    case ShortBuffer shortBuffer -> {
                        var ja = shortBuffer.array();
                        for (var i = 0; i < ja.length; i++) {
                            if (wa[i] == 0) {
                                ja[i] = 0;
                            }
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected buffer type: " + buffer);
                }
            }));
    }

    private AccessorComponentType mapComponentType(ComponentType componentType) {
        return switch (componentType) {
            case Byte -> AccessorComponentType.BYTE;
            case UnsignedByte -> AccessorComponentType.UNSIGNED_BYTE;
            case Short -> AccessorComponentType.SHORT;
            case UnsignedShort -> AccessorComponentType.UNSIGNED_SHORT;
            case UnsignedInt -> AccessorComponentType.UNSIGNED_INT;
            case Float -> AccessorComponentType.FLOAT;
        };
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
}
