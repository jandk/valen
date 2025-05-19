package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.format.gltf.*;
import be.twofold.valen.format.gltf.model.accessor.*;
import be.twofold.valen.format.gltf.model.bufferview.*;
import be.twofold.valen.format.gltf.model.material.*;
import be.twofold.valen.format.gltf.model.mesh.*;
import org.slf4j.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public abstract class GltfModelMapper {
    private static final Logger log = LoggerFactory.getLogger(GltfModelMapper.class);
    private static final Set<Semantic> NORMALIZED = EnumSet.of(Semantic.TEX_COORD, Semantic.COLOR, Semantic.WEIGHTS);

    final GltfContext context;
    final GltfMaterialMapper materialMapper;
    private int numTexCoords = 0;
    private int numColors = 0;
    private int numJoints = 0;
    private int numWeights = 0;

    public GltfModelMapper(GltfContext context) {
        this.context = context;
        this.materialMapper = new GltfMaterialMapper(context);
    }

    Optional<MeshPrimitiveSchema> mapMeshPrimitive(Mesh mesh) throws IOException {
        if (mesh.indexBuffer().count() == 0) {
            log.warn("No indices found for {}, skipping", mesh.name().orElse("<unnamed>"));
            return Optional.empty();
        }

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
            if (semantic == Semantic.COLOR) {
                // TODO: Make Blender ignore vertex colors
                continue;
            }
            var semanticString = mapSemantic(semantic);
            var accessorID = buildAccessor(vertexBuffer, semantic);
            attributes.put(semanticString, accessorID);
        }
        var indices = buildAccessor(mesh.indexBuffer(), null);
        var morphTargets = buildMorphTargets(mesh.blendShapes(), mesh.getBuffer(Semantic.POSITION).orElseThrow().count());

        var meshPrimitive = ImmutableMeshPrimitive.builder()
            .attributes(attributes)
            .indices(indices)
            .material(Optional.ofNullable(materialID))
            .targets(morphTargets)
            .build();
        return Optional.of(meshPrimitive);
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
            .type(GltfModelMapper.mapAccessorType(buffer.info().semantic()));

        if (semantic == Semantic.POSITION) {
            var bounds = Bounds.calculate((FloatBuffer) buffer.buffer());
            accessor
                .min(GltfUtils.mapVector3(bounds.min()))
                .max(GltfUtils.mapVector3(bounds.max()));
        }

        if (isNormalized(buffer)) {
            accessor.normalized(true);
        }

        return context.addAccessor(accessor.build());
    }

    private boolean isNormalized(VertexBuffer<?> accessor) {
        return NORMALIZED.contains(accessor.info().semantic())
            && (accessor.buffer() instanceof ByteBuffer || accessor.buffer() instanceof ShortBuffer);
    }

    void fixJointsAndWeights(Mesh mesh) {
        // TODO: Loop over joints and weights and fix them
        mesh.getBuffer(Semantic.JOINTS).ifPresent(joints -> mesh
            .getBuffer(Semantic.WEIGHTS).ifPresent(weights -> {
                var ja = ((ShortBuffer) joints.buffer()).array();
                var wa = ((FloatBuffer) weights.buffer()).array();
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
            case Semantic.POSITION -> "POSITION";
            case Semantic.NORMAL -> "NORMAL";
            case Semantic.TANGENT -> "TANGENT";
            case Semantic.TEX_COORD -> "TEXCOORD_" + numTexCoords++;
            case Semantic.COLOR -> "COLOR_" + numColors++;
            case Semantic.JOINTS -> "JOINTS_" + numJoints++;
            case Semantic.WEIGHTS -> "WEIGHTS_" + numWeights++;
        };
    }

    static AccessorType mapAccessorType(Semantic semantic) {
        return switch (semantic) {
            case Semantic.TEX_COORD -> AccessorType.VEC2;
            case Semantic.POSITION, Semantic.NORMAL -> AccessorType.VEC3;
            case Semantic.TANGENT, Semantic.COLOR, Semantic.JOINTS, Semantic.WEIGHTS -> AccessorType.VEC4;
            case null -> AccessorType.SCALAR;
        };
    }
}
