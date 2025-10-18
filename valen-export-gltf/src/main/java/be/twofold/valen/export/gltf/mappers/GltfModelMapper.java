package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;
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
        if (mesh.indexBuffer().size() == 0) {
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
            switch (semantic) {
                case COLOR -> {
                    // TODO: Make Blender ignore vertex colors
                }
                case JOINTS -> splitJoints((VertexBuffer<Shorts>) vertexBuffer, attributes);
                case WEIGHTS -> splitWeights((VertexBuffer<Floats>) vertexBuffer, attributes);
                case POSITION -> {
                    var buffer = mesh.getPositions();
                    var bounds = Bounds.calculate(buffer);

                    var bufferView = context.createBufferView(buffer.asBuffer(), BufferViewTarget.ARRAY_BUFFER);
                    var accessorID = context.addAccessor(ImmutableAccessor.builder()
                        .bufferView(bufferView)
                        .componentType(AccessorComponentType.FLOAT)
                        .count(buffer.size() / 3)
                        .type(AccessorType.VEC3)
                        .min(GltfUtils.mapVector3(bounds.min()))
                        .max(GltfUtils.mapVector3(bounds.max()))
                        .build());
                    attributes.put("POSITION", accessorID);
                }
                case NORMAL -> {
                    var buffer = mesh.getNormals().orElseThrow();

                    var bufferView = context.createBufferView(buffer.asBuffer(), BufferViewTarget.ARRAY_BUFFER);
                    var accessorID = context.addAccessor(ImmutableAccessor.builder()
                        .bufferView(bufferView)
                        .componentType(AccessorComponentType.FLOAT)
                        .count(buffer.size() / 3)
                        .type(AccessorType.VEC3)
                        .build());
                    attributes.put("NORMAL", accessorID);
                }
                case TANGENT -> {
                    var buffer = mesh.getTangents().orElseThrow();

                    var bufferView = context.createBufferView(buffer.asBuffer(), BufferViewTarget.ARRAY_BUFFER);
                    var accessorID = context.addAccessor(ImmutableAccessor.builder()
                        .bufferView(bufferView)
                        .componentType(AccessorComponentType.FLOAT)
                        .count(buffer.size() / 4)
                        .type(AccessorType.VEC4)
                        .build());
                    attributes.put("TANGENT", accessorID);
                }
                case TEX_COORD -> {
                    var buffer = ((VertexBuffer<Floats>) vertexBuffer).buffer();

                    var bufferView = context.createBufferView(buffer.asBuffer(), BufferViewTarget.ARRAY_BUFFER);
                    var accessorID = context.addAccessor(ImmutableAccessor.builder()
                        .bufferView(bufferView)
                        .componentType(AccessorComponentType.FLOAT)
                        .count(vertexBuffer.count() / 2)
                        .type(AccessorType.VEC2)
                        .build());
                    attributes.put("TEXCOORD_" + numTexCoords++, accessorID);
                }
            }
        }
        this.numTexCoords = 0;
        this.numColors = 0;
        this.numJoints = 0;
        this.numWeights = 0;

        var indices = buildAccessor(mesh.indexBuffer());
        var morphTargets = buildMorphTargets(mesh.blendShapes(), mesh.getNumTriangles());

        var meshPrimitive = ImmutableMeshPrimitive.builder()
            .attributes(attributes)
            .indices(indices)
            .material(Optional.ofNullable(materialID))
            .targets(morphTargets)
            .build();
        return Optional.of(meshPrimitive);
    }

    private void splitJoints(VertexBuffer<Shorts> vertexBuffer, Map<String, AccessorID> attributes) throws IOException {
        int numBuffers = (vertexBuffer.info().size() + 3) / 4;
        for (int b = 0; b < numBuffers; b++) {
            var offset = b * 4;
            var values = Math.min(4, vertexBuffer.info().size() - offset);
            var joints = MutableShorts.allocate(vertexBuffer.count() * 4);
            for (int i = offset, o = 0; i < vertexBuffer.buffer().size(); i += vertexBuffer.info().size(), o += 4) {
                for (int j = 0; j < values; j++) {
                    joints.setShort(o + j, vertexBuffer.buffer().getShort(i + j));
                }
                for (int j = values; j < 4; j++) {
                    joints.setShort(o + j, (short) 0);
                }
            }

            var bufferView = context.createBufferView(joints.asBuffer(), BufferViewTarget.ARRAY_BUFFER);
            var accessorID = context.addAccessor(ImmutableAccessor.builder()
                .bufferView(bufferView)
                .componentType(AccessorComponentType.UNSIGNED_SHORT)
                .count(joints.size() / 4)
                .type(AccessorType.VEC4)
                .build());
            attributes.put("JOINTS_" + numJoints++, accessorID);
        }
    }

    private void splitWeights(VertexBuffer<Floats> vertexBuffer, Map<String, AccessorID> attributes) throws IOException {
        int numBuffers = (vertexBuffer.info().size() + 3) / 4;
        for (int b = 0; b < numBuffers; b++) {
            var offset = b * 4;
            var values = Math.min(4, vertexBuffer.info().size() - offset);
            var weights = MutableFloats.allocate(vertexBuffer.count() * 4);
            for (int i = offset, o = 0; i < vertexBuffer.buffer().size(); i += vertexBuffer.info().size(), o += 4) {
                for (int j = 0; j < values; j++) {
                    weights.setFloat(o + j, vertexBuffer.buffer().getFloat(i + j));
                }
                for (int j = values; j < 4; j++) {
                    weights.setFloat(o + j, (short) 0);
                }
            }

            var bufferView = context.createBufferView(weights.asBuffer(), BufferViewTarget.ARRAY_BUFFER);
            var accessorID = context.addAccessor(ImmutableAccessor.builder()
                .bufferView(bufferView)
                .componentType(AccessorComponentType.FLOAT)
                .count(weights.size() / 4)
                .type(AccessorType.VEC4)
                .build());

            attributes.put("WEIGHTS_" + numWeights++, accessorID);
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

    private AccessorID buildAccessor(Ints buffer) throws IOException {
        var bufferView = context.createBufferView(buffer.asBuffer(), BufferViewTarget.ELEMENT_ARRAY_BUFFER);

        var accessor = ImmutableAccessor.builder()
            .bufferView(bufferView)
            .componentType(AccessorComponentType.UNSIGNED_INT)
            .count(buffer.size() / 3)
            .type(AccessorType.SCALAR);

        return context.addAccessor(accessor.build());
    }

    private AccessorID buildAccessor(VertexBuffer<?> buffer, Semantic semantic) throws IOException {
        var bufferView = context.createBufferView(buffer.buffer().asBuffer(), BufferViewTarget.ARRAY_BUFFER);

        var accessor = ImmutableAccessor.builder()
            .bufferView(bufferView)
            .componentType(mapComponentType(buffer.buffer().asBuffer()))
            .count(buffer.count())
            .type(mapAccessorType(buffer.info().semantic()));

        if (semantic == Semantic.POSITION) {
            var bounds = Bounds.calculate((FloatBuffer) buffer.buffer().asBuffer());
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
            && (accessor.buffer() instanceof Bytes || accessor.buffer() instanceof Shorts);
    }

    void fixJointsAndWeights(Mesh mesh) {
        // TODO: Loop over joints and weights and fix them
        mesh.getBuffer(Semantic.JOINTS).ifPresent(joints -> mesh
            .getBuffer(Semantic.WEIGHTS).ifPresent(weights -> {
                var jb = (MutableShorts) joints.buffer();
                var wb = (MutableFloats) weights.buffer();
                for (var i = 0; i < jb.size(); i++) {
                    if (wb.getFloat(i) == 0) {
                        jb.setShort(i, (short) 0);
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

    AccessorType mapAccessorType(Semantic semantic) {
        return switch (semantic) {
            case Semantic.TEX_COORD -> AccessorType.VEC2;
            case Semantic.POSITION, Semantic.NORMAL -> AccessorType.VEC3;
            case Semantic.TANGENT, Semantic.COLOR, Semantic.JOINTS, Semantic.WEIGHTS -> AccessorType.VEC4;
            case null -> AccessorType.SCALAR;
        };
    }
}
