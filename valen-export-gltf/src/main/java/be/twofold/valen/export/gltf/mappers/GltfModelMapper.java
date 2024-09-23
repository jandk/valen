package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.accessor.*;
import be.twofold.valen.gltf.model.buffer.*;
import com.google.gson.*;

import java.nio.*;

public final class GltfModelMapper {
    private final GltfContext context;

    public GltfModelMapper(GltfContext context) {
        this.context = context;
    }

    public MeshSchema map(Model model) {
        // First we do the meshes
        var primitives = model.meshes().stream()
            .map(this::mapMesh)
            .toList();

        return MeshSchema.builder()
            .name(model.name())
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
            attributes.addProperty(semantic, accessor.id());
        }

        var faceAccessor = buildAccessor(mesh.faceBuffer(), null);
        return MeshPrimitiveSchema.builder()
            .material(MaterialId.of(mesh.materialIndex()))
            .attributes(attributes)
            .indices(faceAccessor)
            .build();
    }

    private AccessorID buildAccessor(VertexBuffer buffer, Semantic semantic) {
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
            case Scalar -> AccessorType.Scalar;
            case Vector2 -> AccessorType.Vector2;
            case Vector3 -> AccessorType.Vector3;
            case Vector4 -> AccessorType.Vector4;
            case Matrix2 -> AccessorType.Matrix2;
            case Matrix3 -> AccessorType.Matrix3;
            case Matrix4 -> AccessorType.Matrix4;
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
