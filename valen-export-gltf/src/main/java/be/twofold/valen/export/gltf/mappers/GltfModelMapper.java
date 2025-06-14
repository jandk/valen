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
            } else if (semantic == Semantic.JOINTS) {
                splitJoints((VertexBuffer<ShortBuffer>) vertexBuffer, attributes);
            } else if (semantic == Semantic.WEIGHTS) {
                splitWeights((VertexBuffer<FloatBuffer>) vertexBuffer, attributes);
            } else {
                var semanticString = mapSemantic(semantic);
                var accessorID = buildAccessor(vertexBuffer, semantic);
                attributes.put(semanticString, accessorID);
            }
        }
        this.numTexCoords = 0;
        this.numColors = 0;
        this.numJoints = 0;
        this.numWeights = 0;

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

    private void splitJoints(VertexBuffer<ShortBuffer> vertexBuffer, Map<String, AccessorID> attributes) throws IOException {
        int numBuffers = (vertexBuffer.info().size() + 3) / 4;
        for (int b = 0; b < numBuffers; b++) {
            var offset = b * 4;
            var values = Math.min(4, vertexBuffer.info().size() - offset);
            var joints = ShortBuffer.allocate(vertexBuffer.count() * 4);
            for (int i = offset, o = 0; i < vertexBuffer.buffer().limit(); i += vertexBuffer.info().size(), o += 4) {
                for (int j = 0; j < values; j++) {
                    joints.put(o + j, vertexBuffer.buffer().get(i + j));
                }
                for (int j = values; j < 4; j++) {
                    joints.put(o + j, (short) 0);
                }
            }

            var newVertexBuffer = new VertexBuffer<>(joints, VertexBufferInfo.joints(ComponentType.UNSIGNED_SHORT, 4));
            var semanticString = mapSemantic(Semantic.JOINTS);
            var accessorID = buildAccessor(newVertexBuffer, Semantic.JOINTS);
            attributes.put(semanticString, accessorID);
        }
    }

    private void splitWeights(VertexBuffer<FloatBuffer> vertexBuffer, Map<String, AccessorID> attributes) throws IOException {
        int numBuffers = (vertexBuffer.info().size() + 3) / 4;
        for (int b = 0; b < numBuffers; b++) {
            var offset = b * 4;
            var values = Math.min(4, vertexBuffer.info().size() - offset);
            var weights = FloatBuffer.allocate(vertexBuffer.count() * 4);
            for (int i = offset, o = 0; i < vertexBuffer.buffer().limit(); i += vertexBuffer.info().size(), o += 4) {
                for (int j = 0; j < values; j++) {
                    weights.put(o + j, vertexBuffer.buffer().get(i + j));
                }
                for (int j = values; j < 4; j++) {
                    weights.put(o + j, (short) 0);
                }
            }

            var newVertexBuffer = new VertexBuffer<>(weights, VertexBufferInfo.weights(ComponentType.FLOAT, 4));
            var semanticString = mapSemantic(Semantic.WEIGHTS);
            var accessorID = buildAccessor(newVertexBuffer, Semantic.WEIGHTS);
            attributes.put(semanticString, accessorID);
        }
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
            .componentType(mapComponentType(buffer.buffer()))
            .count(buffer.count())
            .type(mapAccessorType(buffer.info().semantic()));

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
                var jb = (ShortBuffer) joints.buffer();
                var wb = (FloatBuffer) weights.buffer();
                for (var i = 0; i < jb.limit(); i++) {
                    if (wb.get(i) == 0) {
                        jb.put(i, (short) 0);
                    }
                }
            }));
    }

    AccessorComponentType mapComponentType(Buffer buffer) {
        return switch (buffer) {
            case ByteBuffer _ -> AccessorComponentType.UNSIGNED_BYTE;
            case ShortBuffer _ -> AccessorComponentType.UNSIGNED_SHORT;
            case IntBuffer _ -> AccessorComponentType.UNSIGNED_INT;
            case FloatBuffer _ -> AccessorComponentType.FLOAT;
            default -> throw new UnsupportedOperationException("Unsupported buffer type: " + buffer);
        };
    }

    AccessorType mapElementType(ElementType type) {
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

    AccessorType mapAccessorType(Semantic semantic) {
        return switch (semantic) {
            case Semantic.TEX_COORD -> AccessorType.VEC2;
            case Semantic.POSITION, Semantic.NORMAL -> AccessorType.VEC3;
            case Semantic.TANGENT, Semantic.COLOR, Semantic.JOINTS, Semantic.WEIGHTS -> AccessorType.VEC4;
            case null -> AccessorType.SCALAR;
        };
    }
}
