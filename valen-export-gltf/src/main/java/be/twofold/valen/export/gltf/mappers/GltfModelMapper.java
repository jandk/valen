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
        if (mesh.material().isPresent()) {
            materialID = materialMapper.map(mesh.material().get());
        }

        // Have to fix up the joints and weights first
        fixJointsAndWeights(mesh);

        var attributes = new HashMap<String, AccessorID>();
        for (var vertexBuffer : mesh.vertexBuffers()) {
            var semantic = vertexBuffer.info().semantic();
            if (semantic instanceof Semantic.Color) {
                // TODO: Make Blender ignore vertex colors
                continue;
            }
            var semanticString = mapSemantic(semantic);
            var accessorID = buildAccessor(vertexBuffer, semantic);
            attributes.put(semanticString, accessorID);
        }
        var indices = buildAccessor(mesh.indexBuffer(), null);
        var morphTargets = buildMorphTargets(mesh.blendShapes(), mesh.getBuffer(Semantic.POSITION).orElseThrow().count());

        return ImmutableMeshPrimitive.builder()
            .attributes(attributes)
            .indices(indices)
            .material(Optional.ofNullable(materialID))
            .targets(morphTargets)
            .build();
    }

    private List<Map<String, AccessorID>> buildMorphTargets(List<BlendShape> blendShapes, int count) throws IOException {
        var morphTargets = new ArrayList<Map<String, AccessorID>>();
        for (var blendShape : blendShapes) {
            var indexBufferView = context.createBufferView(blendShape.indices(), null);
            var indices = ImmutableAccessorSparseIndices.builder()
                .bufferView(indexBufferView)
                .componentType(AccessorComponentType.UNSIGNED_SHORT)
                .build();

            var valuesBufferView = context.createBufferView(blendShape.values(), null);
            var values = ImmutableAccessorSparseValues.builder()
                .bufferView(valuesBufferView)
                .build();

            var accessorSparse = ImmutableAccessorSparse.builder()
                .count(blendShape.indices().capacity())
                .indices(indices)
                .values(values)
                .build();

            var bounds = Bounds.calculate(blendShape.values());
            var accessor = ImmutableAccessor.builder()
                .componentType(AccessorComponentType.FLOAT)
                .count(count)
                .type(AccessorType.VEC3)
                .sparse(accessorSparse)
                .min(GltfUtils.mapVector3(bounds.min()))
                .max(GltfUtils.mapVector3(bounds.max()))
                .build();
            var accessorID = context.addAccessor(accessor);

            morphTargets.add(Map.of("POSITION", accessorID));
        }
        return morphTargets;
    }

    private AccessorID buildAccessor(VertexBuffer<?> buffer, Semantic semantic) throws IOException {
        var target = semantic == null
            ? BufferViewTarget.ELEMENT_ARRAY_BUFFER
            : BufferViewTarget.ARRAY_BUFFER;

        var bufferView = context.createBufferView(buffer.buffer(), target);

        var accessor = ImmutableAccessor.builder()
            .bufferView(bufferView)
            .componentType(mapComponentType(buffer.info().componentType()))
            .count(buffer.count())
            .type(GltfModelMapper.mapElementType(buffer.info().elementType()));

        if (semantic == Semantic.POSITION) {
            var bounds = Bounds.calculate((FloatBuffer) buffer.buffer());
            accessor
                .min(GltfUtils.mapVector3(bounds.min()))
                .max(GltfUtils.mapVector3(bounds.max()));
        }

        if (buffer.info().normalized()) {
            accessor.normalized(true);
        }

        return context.addAccessor(accessor.build());
    }

    void fixJointsAndWeights(Mesh mesh) {
        // TODO: Loop over joints and weights and fix them
        mesh.getBuffer(Semantic.JOINTS0).ifPresent(joints -> mesh
            .getBuffer(Semantic.WEIGHTS0).ifPresent(weights -> {
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
        if (componentType == ComponentType.BYTE) {
            return AccessorComponentType.BYTE;
        } else if (componentType == ComponentType.UNSIGNED_BYTE) {
            return AccessorComponentType.UNSIGNED_BYTE;
        } else if (componentType == ComponentType.SHORT) {
            return AccessorComponentType.SHORT;
        } else if (componentType == ComponentType.UNSIGNED_SHORT) {
            return AccessorComponentType.UNSIGNED_SHORT;
        } else if (componentType == ComponentType.UNSIGNED_INT) {
            return AccessorComponentType.UNSIGNED_INT;
        } else if (componentType == ComponentType.FLOAT) {
            return AccessorComponentType.FLOAT;
        } else {
            throw new UnsupportedOperationException("Unsupported component type: " + componentType);
        }
    }

    static AccessorType mapElementType(ElementType type) {
        return switch (type) {
            case SCALAR -> AccessorType.SCALAR;
            case VECTOR2 -> AccessorType.VEC2;
            case VECTOR3 -> AccessorType.VEC3;
            case VECTOR4 -> AccessorType.VEC4;
            case MATRIX2 -> AccessorType.MAT2;
            case MATRIX3 -> AccessorType.MAT3;
            case MATRIX4 -> AccessorType.MAT4;
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
