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
import java.util.*;

public abstract class GltfModelMapper {
    private static final Logger log = LoggerFactory.getLogger(GltfModelMapper.class);

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
        if (mesh.indexBuffer().isEmpty()) {
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

        var vertexBuffer = mesh.vertexBuffer();
        var attributes = new HashMap<String, AccessorID>();
        attributes.put("POSITION", buildAccessor(vertexBuffer.positions(), AccessorComponentType.FLOAT, AccessorType.VEC3, true));
        vertexBuffer.normals().ifPresent(floats -> attributes.put("NORMAL", buildAccessor(floats, AccessorComponentType.FLOAT, AccessorType.VEC3, false)));
        vertexBuffer.tangents().ifPresent(floats -> attributes.put("TANGENT", buildAccessor(floats, AccessorComponentType.FLOAT, AccessorType.VEC4, false)));
        vertexBuffer.texCoords().forEach(floats -> attributes.put("TEXCOORD_" + numTexCoords++, buildAccessor(floats, AccessorComponentType.FLOAT, AccessorType.VEC2, false)));
        vertexBuffer.joints().ifPresent(shorts -> splitJoints(shorts, vertexBuffer.maximumInfluence(), attributes));
        vertexBuffer.weights().ifPresent(floats -> splitWeights(floats, vertexBuffer.maximumInfluence(), attributes));
        // We don't do anything with colors, as Blender likes to incorporate vertex colors

        this.numTexCoords = 0;
        this.numColors = 0;
        this.numJoints = 0;
        this.numWeights = 0;

        var indices = buildAccessor(mesh.indexBuffer().indices());
        var morphTargets = buildMorphTargets(mesh.blendShapes(), mesh.indexBuffer().faceCount());

        var meshPrimitive = ImmutableMeshPrimitive.builder()
            .attributes(attributes)
            .indices(indices)
            .material(Optional.ofNullable(materialID))
            .targets(morphTargets)
            .build();
        return Optional.of(meshPrimitive);
    }

    private BufferViewID createBufferView(WrappedArray array, BufferViewTarget target) {
        try {
            return context.createBufferView(array.asBuffer(), target);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void splitJoints(Shorts shorts, int maximumInfluence, Map<String, AccessorID> attributes) {
        int numBuffers = (maximumInfluence + 3) / 4;
        int numVertices = shorts.size() / maximumInfluence;

        for (int b = 0; b < numBuffers; b++) {
            var offset = b * 4;
            var values = Math.min(4, maximumInfluence - offset);
            var joints = MutableShorts.allocate(numVertices * 4);
            for (int i = offset, o = 0; i < shorts.size(); i += maximumInfluence, o += 4) {
                for (int j = 0; j < values; j++) {
                    joints.setShort(o + j, shorts.getShort(i + j));
                }
                for (int j = values; j < 4; j++) {
                    joints.setShort(o + j, (short) 0);
                }
            }
            attributes.put("JOINTS_" + numJoints++, buildAccessor(joints, AccessorComponentType.UNSIGNED_SHORT, AccessorType.VEC4, false));
        }
    }

    private void splitWeights(Floats floats, int maximumInfluence, Map<String, AccessorID> attributes) {
        int numBuffers = (maximumInfluence + 3) / 4;
        int numVertices = floats.size() / maximumInfluence;

        for (int b = 0; b < numBuffers; b++) {
            var offset = b * 4;
            var values = Math.min(4, maximumInfluence - offset);
            var weights = MutableFloats.allocate(numVertices * 4);
            for (int i = offset, o = 0; i < floats.size(); i += maximumInfluence, o += 4) {
                for (int j = 0; j < values; j++) {
                    weights.setFloat(o + j, floats.getFloat(i + j));
                }
                for (int j = values; j < 4; j++) {
                    weights.setFloat(o + j, (short) 0);
                }
            }
            attributes.put("WEIGHTS_" + numWeights++, buildAccessor(weights, AccessorComponentType.FLOAT, AccessorType.VEC4, false));
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

    private AccessorID buildAccessor(Ints buffer) {
        var bufferView = createBufferView(buffer, BufferViewTarget.ELEMENT_ARRAY_BUFFER);

        var accessor = ImmutableAccessor.builder()
            .bufferView(bufferView)
            .componentType(AccessorComponentType.UNSIGNED_INT)
            .count(buffer.size())
            .type(AccessorType.SCALAR);

        return context.addAccessor(accessor.build());
    }

    private AccessorID buildAccessor(WrappedArray buffer, AccessorComponentType componentType, AccessorType type, boolean withBounds) {
        var bufferView = createBufferView(buffer, BufferViewTarget.ARRAY_BUFFER);
        var builder = ImmutableAccessor.builder()
            .bufferView(bufferView)
            .componentType(componentType)
            .count(buffer.size() / type.size())
            .type(type);

        if (withBounds) {
            Bounds bounds = Bounds.calculate((Floats) buffer);
            builder
                .min(GltfUtils.mapVector3(bounds.min()))
                .max(GltfUtils.mapVector3(bounds.max()));
        }

        return context.addAccessor(builder.build());
    }

    void fixJointsAndWeights(Mesh mesh) {
        // TODO: Loop over joints and weights and fix them
        mesh.vertexBuffer().joints().ifPresent(joints ->
            mesh.vertexBuffer().weights().ifPresent(weights -> {
                var jb = (MutableShorts) joints;
                var wb = (MutableFloats) weights;
                for (var i = 0; i < jb.size(); i++) {
                    if (wb.getFloat(i) == 0) {
                        jb.setShort(i, (short) 0);
                    }
                }
            }));
    }
}
