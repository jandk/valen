package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.format.gltf.*;
import be.twofold.valen.format.gltf.model.accessor.*;
import be.twofold.valen.format.gltf.model.bufferview.*;
import be.twofold.valen.format.gltf.model.material.*;
import be.twofold.valen.format.gltf.model.mesh.*;
import org.slf4j.*;
import wtf.reversed.toolbox.collect.*;

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
        if (mesh.faceCount() == 0) {
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
        attributes.put("POSITION", buildAccessor(mesh.positions(), AccessorComponentType.FLOAT, AccessorType.VEC3, true));
        mesh.normals().ifPresent(floats -> attributes.put("NORMAL", buildAccessor(floats, AccessorComponentType.FLOAT, AccessorType.VEC3, false)));
        mesh.tangents().ifPresent(floats -> attributes.put("TANGENT", buildAccessor(floats, AccessorComponentType.FLOAT, AccessorType.VEC4, false)));
        mesh.texCoords().forEach(floats -> attributes.put("TEXCOORD_" + numTexCoords++, buildAccessor(floats, AccessorComponentType.FLOAT, AccessorType.VEC2, false)));
        mesh.joints().ifPresent(shorts -> splitJoints(shorts, mesh.maxInfluence(), attributes));
        mesh.weights().ifPresent(floats -> splitWeights(floats, mesh.maxInfluence(), attributes));
        mesh.custom().forEach((name, vertexBuffer) -> {
            var sanitizedName = "_" + name.toUpperCase(Locale.ROOT);
            var componentType = mapComponentType(vertexBuffer.componentType());
            var accessorType = mapElementType(vertexBuffer.elementType());
            attributes.put(sanitizedName, buildAccessor(vertexBuffer.array(), componentType, accessorType, false));
        });
        // We don't do anything with colors, as Blender likes to incorporate vertex colors

        this.numTexCoords = 0;
        this.numColors = 0;
        this.numJoints = 0;
        this.numWeights = 0;

        var indices = buildAccessor(mesh.indices());
        var morphTargets = buildMorphTargets(mesh.blendShapes(), mesh.faceCount());

        var meshPrimitive = ImmutableMeshPrimitive.builder()
            .attributes(attributes)
            .indices(indices)
            .material(Optional.ofNullable(materialID))
            .targets(morphTargets)
            .build();
        return Optional.of(meshPrimitive);
    }

    private AccessorComponentType mapComponentType(ComponentType<?> componentType) {
        if (componentType == ComponentType.UNSIGNED_BYTE) {
            return AccessorComponentType.UNSIGNED_BYTE;
        } else if (componentType == ComponentType.UNSIGNED_SHORT) {
            return AccessorComponentType.UNSIGNED_SHORT;
        } else if (componentType == ComponentType.UNSIGNED_INT) {
            return AccessorComponentType.UNSIGNED_INT;
        } else if (componentType == ComponentType.FLOAT) {
            return AccessorComponentType.FLOAT;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private AccessorType mapElementType(ElementType elementType) {
        return switch (elementType) {
            case SCALAR -> AccessorType.SCALAR;
            case VECTOR2 -> AccessorType.VEC2;
            case VECTOR3 -> AccessorType.VEC3;
            case VECTOR4 -> AccessorType.VEC4;
            case MATRIX2 -> AccessorType.MAT2;
            case MATRIX3 -> AccessorType.MAT3;
            case MATRIX4 -> AccessorType.MAT4;
        };
    }

    private BufferViewID createBufferView(Slice slice, BufferViewTarget target) {
        try {
            return context.createBufferView(slice.asBuffer(), target);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void splitJoints(Shorts shorts, int maxInfluence, Map<String, AccessorID> attributes) {
        int numBuffers = (maxInfluence + 3) / 4;
        int numVertices = shorts.length() / maxInfluence;

        for (int b = 0; b < numBuffers; b++) {
            var offset = b * 4;
            var values = Math.min(4, maxInfluence - offset);
            var joints = Shorts.Mutable.allocate(numVertices * 4);
            for (int i = offset, o = 0; i < shorts.length(); i += maxInfluence, o += 4) {
                for (int j = 0; j < values; j++) {
                    joints.set(o + j, shorts.get(i + j));
                }
                for (int j = values; j < 4; j++) {
                    joints.set(o + j, (short) 0);
                }
            }
            attributes.put("JOINTS_" + numJoints++, buildAccessor(joints, AccessorComponentType.UNSIGNED_SHORT, AccessorType.VEC4, false));
        }
    }

    private void splitWeights(Floats floats, int maxInfluence, Map<String, AccessorID> attributes) {
        int numBuffers = (maxInfluence + 3) / 4;
        int numVertices = floats.length() / maxInfluence;

        for (int b = 0; b < numBuffers; b++) {
            var offset = b * 4;
            var values = Math.min(4, maxInfluence - offset);
            var weights = Floats.Mutable.allocate(numVertices * 4);
            for (int i = offset, o = 0; i < floats.length(); i += maxInfluence, o += 4) {
                for (int j = 0; j < values; j++) {
                    weights.set(o + j, floats.get(i + j));
                }
                for (int j = values; j < 4; j++) {
                    weights.set(o + j, (short) 0);
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
            .count(buffer.length())
            .type(AccessorType.SCALAR);

        return context.addAccessor(accessor.build());
    }

    private AccessorID buildAccessor(Slice slice, AccessorComponentType componentType, AccessorType type, boolean withBounds) {
        var bufferView = createBufferView(slice, BufferViewTarget.ARRAY_BUFFER);
        var builder = ImmutableAccessor.builder()
            .bufferView(bufferView)
            .componentType(componentType)
            .count(slice.length() / type.size())
            .type(type);

        if (withBounds) {
            Bounds bounds = Bounds.calculate((Floats) slice);
            builder
                .min(GltfUtils.mapVector3(bounds.min()))
                .max(GltfUtils.mapVector3(bounds.max()));
        }

        return context.addAccessor(builder.build());
    }

    private void fixJointsAndWeights(Mesh mesh) {
        mesh.joints().map(Shorts.Mutable.class::cast).ifPresent(joints ->
            mesh.weights().map(Floats.Mutable.class::cast).ifPresent(weights -> {
                for (var i = 0; i < joints.length(); i++) {
                    if (weights.get(i) == 0) {
                        joints.set(i, (short) 0);
                    }
                }
            }));
    }
}
