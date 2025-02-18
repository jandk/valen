package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.format.gltf.*;
import be.twofold.valen.format.gltf.model.accessor.*;
import be.twofold.valen.format.gltf.model.bufferview.*;
import be.twofold.valen.format.gltf.model.material.*;
import be.twofold.valen.format.gltf.model.mesh.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public abstract class GltfModelMapper {
    final GltfContext context;
    final GltfMaterialMapper materialMapper;

    public GltfModelMapper(GltfContext context) {
        this.context = context;
        this.materialMapper = new GltfMaterialMapper(context);
    }

    MeshPrimitiveSchema mapMeshPrimitive(Mesh mesh) throws IOException {
        // Add the material
        var materialID = (MaterialID) null;
        if (mesh.materialOpt().isPresent()) {
            materialID = materialMapper.map(mesh.materialOpt().get());
        }

        // Have to fix up the joints and weights first
        fixJointsAndWeights(mesh);

        var attributes = new HashMap<String, AccessorID>();
        for (var entry : mesh.vertexBuffers().entrySet()) {
            if (entry.getKey() instanceof Semantic.Color) {
                // TODO: Make Blender ignore vertex colors
                continue;
            }
            var semantic = mapSemantic(entry.getKey());
            var accessorID = buildAccessor(entry.getValue(), entry.getKey());
            attributes.put(semantic, accessorID);
        }

        var indices = buildAccessor(mesh.indexBuffer(), null);
        return ImmutableMeshPrimitive.builder()
            .attributes(attributes)
            .indices(indices)
            .material(Optional.ofNullable(materialID))
            .build();
    }

    private AccessorID buildAccessor(VertexBuffer buffer, Semantic semantic) throws IOException {
        var target = semantic == null
            ? BufferViewTarget.ELEMENT_ARRAY_BUFFER
            : BufferViewTarget.ARRAY_BUFFER;

        var bufferView = context.createBufferView(buffer.buffer(), target);

        var accessor = ImmutableAccessor.builder()
            .bufferView(bufferView)
            .componentType(mapComponentType(buffer.componentType()))
            .count(buffer.count())
            .type(GltfModelMapper.mapElementType(buffer.elementType()));

        if (semantic == Semantic.Position) {
            var bounds = Bounds.calculate((FloatBuffer) buffer.buffer());
            accessor
                .min(GltfUtils.mapVector3(bounds.min()))
                .max(GltfUtils.mapVector3(bounds.max()));
        }

        if (buffer.normalized()) {
            accessor.normalized(true);
        }

        return context.addAccessor(accessor.build());
    }

    void fixJointsAndWeights(Mesh mesh) {
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

    AccessorComponentType mapComponentType(ComponentType<?> componentType) {
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

    static AccessorType mapElementType(ElementType type) {
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

    String mapSemantic(Semantic semantic) {
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
