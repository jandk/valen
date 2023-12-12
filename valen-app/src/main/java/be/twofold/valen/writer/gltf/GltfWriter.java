package be.twofold.valen.writer.gltf;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.geometry.*;
import be.twofold.valen.reader.md6skl.*;
import be.twofold.valen.writer.gltf.gson.*;
import be.twofold.valen.writer.gltf.model.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public final class GltfWriter {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(AccessorComponentType.class, new AccessorComponentTypeTypeAdapter())
        .registerTypeAdapter(BufferViewTarget.class, new BufferViewTargetTypeAdapter().nullSafe())
        .registerTypeAdapter(Quaternion.class, new QuaternionTypeAdapter().nullSafe())
        .registerTypeAdapter(Vector2.class, new Vector2TypeAdapter())
        .registerTypeAdapter(Vector3.class, new Vector3TypeAdapter().nullSafe())
        .registerTypeAdapter(Vector4.class, new Vector4TypeAdapter())
        .create();

    private final WritableByteChannel channel;
    private final List<Mesh> sourceMeshes;
    private final Md6Skeleton skeleton;
    private final Animation animation;

    private final List<AccessorSchema> accessors = new ArrayList<>();
    private final List<BufferViewSchema> bufferViews = new ArrayList<>();
    private final List<BufferSchema> buffers = new ArrayList<>();
    private final List<MeshSchema> meshes = new ArrayList<>();
    private final List<NodeSchema> nodes = new ArrayList<>();
    private final List<SceneSchema> scenes = new ArrayList<>();
    private final List<SkinSchema> skins = new ArrayList<>();
    private final List<AnimationSchema> animations = new ArrayList<>();

    private int bufferOffset;
    private int skeletonNode;

    public GltfWriter(
        WritableByteChannel channel,
        List<Mesh> sourceMeshes,
        Md6Skeleton skeleton,
        Animation animation
    ) {
        this.channel = channel;
        this.sourceMeshes = sourceMeshes;
        this.skeleton = skeleton;
        this.animation = animation;
    }

    public void write() {
        buildMeshes();
        buildNodes();
        buildScenes();
        if (skeleton != null) {
            buildSkeleton();
            buildAnimations();

            // Ugly little fixup
            scenes.get(0).nodes().add(skeletonNode);
        }
        buildBuffers(); // Only now we know the buffer offset

        try {
            GltfSchema gltf = buildGltf();

            String json = GSON.toJson(gltf);
            String prettyJson = GSON.newBuilder().setPrettyPrinting().create().toJson(gltf);
            Files.writeString(Path.of("C:\\Temp\\gltf.json"), prettyJson);

            byte[] rawJson = json.getBytes(StandardCharsets.US_ASCII);
            int alignedJsonLength = alignedLength(rawJson.length);

            int totalSize = 12 + 8 + alignedJsonLength + 8 + bufferOffset;
            channel.write(GlbHeader.of(totalSize).toBuffer());
            channel.write(GlbChunkHeader.of(GlbChunkType.Json, alignedJsonLength).toBuffer());
            channel.write(ByteBuffer.wrap(rawJson));
            align(rawJson.length, (byte) ' ');
            channel.write(GlbChunkHeader.of(GlbChunkType.Bin, bufferOffset).toBuffer());
            writeBuffers();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private GltfSchema buildGltf() {
        AssetSchema asset = new AssetSchema("valen", "2.0");

        return new GltfSchema(
            asset,
            accessors,
            bufferViews,
            buffers,
            meshes,
            nodes,
            scenes,
            skeleton != null ? skins : null,
            animation != null ? animations : null,
            0
        );
    }


    private void buildBuffers() {
        BufferSchema buffer = new BufferSchema(bufferOffset);
        buffers.add(buffer);
    }

    public void buildMeshes() {
        // First we do the meshes
        List<PrimitiveSchema> primitives = sourceMeshes.stream()
            .map(this::buildMeshPrimitive)
            .toList();

        MeshSchema mesh = new MeshSchema(primitives);
        meshes.add(mesh);
    }

    private PrimitiveSchema buildMeshPrimitive(Mesh mesh) {
        JsonObject attributes = new JsonObject();
        Bounds bounds = calculateBounds(mesh.positions());
        attributes.addProperty("POSITION", buildAccessor(mesh.positions().capacity(), BufferType.Position, bounds.min().toArray(), bounds.max().toArray()));
        attributes.addProperty("NORMAL", buildAccessor(mesh.normals().capacity(), BufferType.Normal, null, null));
        attributes.addProperty("TANGENT", buildAccessor(mesh.tangents().capacity(), BufferType.Tangent, null, null));

        if (mesh.texCoords() != null) {
            attributes.addProperty("TEXCOORD_0", buildAccessor(mesh.texCoords().capacity(), BufferType.TexCoordN, null, null));
        }

        if (skeleton != null) {
            fixJointsWithEmptyWeights(mesh.joints(), mesh.weights());
            attributes.addProperty("JOINTS_0", buildAccessor(mesh.joints().capacity(), BufferType.JointsN, null, null));
            attributes.addProperty("WEIGHTS_0", buildAccessor(mesh.weights().capacity(), BufferType.WeightsN, null, null));
        } else {
            if (mesh.colors() != null) {
                attributes.addProperty("COLOR_0", buildAccessor(mesh.colors().capacity(), BufferType.ColorN, null, null));
            }
        }

        return new PrimitiveSchema(
            attributes,
            buildAccessor(mesh.indices().capacity(), BufferType.Indices, null, null)
        );
    }

    private void fixJointsWithEmptyWeights(ByteBuffer joints, ByteBuffer weights) {
        byte[] ja = joints.array();
        byte[] wa = weights.array();
        for (int i = 0; i < ja.length; i++) {
            if (wa[i] == 0) {
                ja[i] = 0;
            }
        }
    }

    private int buildAccessor(int capacity, BufferType bufferType, float[] min, float[] max) {
        int bufferView = buildBufferView(capacity, bufferType, bufferType.getComponentType());
        return buildAccessor(bufferView, capacity, bufferType, min, max);
    }

    private int buildAccessor(int bufferView, int count, BufferType type, float[] min, float[] max) {
        int dataTypeSize = type.getDataType().getSize();
        assert count % dataTypeSize == 0 : "Count must be a multiple of " + dataTypeSize;

        AccessorSchema accessor = new AccessorSchema(
            bufferView,
            type.getComponentType(),
            count / dataTypeSize,
            type.getDataType(),
            min,
            max,
            type.isNormalized() ? true : null
        );
        accessors.add(accessor);
        return accessors.size() - 1;
    }

    private int buildBufferView(int length, BufferType bufferType, AccessorComponentType componentType) {
        int byteLength = length * componentType.getSize();

        BufferViewTarget target = switch (bufferType) {
            case InverseBind, KeyFrame, Rotation, ScaleTranslation -> null;
            case Indices -> BufferViewTarget.ELEMENT_ARRAY_BUFFER;
            default -> BufferViewTarget.ARRAY_BUFFER;
        };

        BufferViewSchema bufferView = new BufferViewSchema(
            0,
            bufferOffset,
            byteLength,
            target
        );

        bufferViews.add(bufferView);

        // Round up offset to a multiple of 4
        bufferOffset = alignedLength(bufferOffset + byteLength);
        return bufferViews.size() - 1;
    }

    private void buildNodes() {
        Integer skin = skeleton != null ? 0 : null;
        NodeSchema meshSkin = NodeSchema.buildMeshSkin(0, skin);
        nodes.add(meshSkin);

        NodeSchema rst = NodeSchema.buildRST(List.of(nodes.size() - 1));
        nodes.add(rst);
    }

    private void buildScenes() {
        List<Integer> nodes = new ArrayList<>();
        nodes.add(0);

        SceneSchema scene = new SceneSchema(nodes);
        scenes.add(scene);
    }

    private void buildSkeleton() {
        int offset = nodes.size();
        List<Bone> joints = skeleton.joints();

        // Calculate the parent-child relationships
        Map<Integer, List<Integer>> children = new HashMap<>();
        for (int i = 0; i < joints.size(); i++) {
            Bone joint = joints.get(i);
            children
                .computeIfAbsent(joint.parent(), __ -> new ArrayList<>())
                .add(offset + i);
        }

        // Build the skeleton
        List<Integer> jointIndices = new ArrayList<>();
        for (int i = 0; i < joints.size(); i++) {
            if (joints.get(i).parent() == -1) {
                skeletonNode = offset + i;
            }
            jointIndices.add(offset + i);
            buildSkeletonJoint(joints.get(i), children.getOrDefault(i, List.of()));
        }

        // Build the skin
        buildSkeletonSkin(jointIndices);
    }

    private void buildSkeletonJoint(Bone joint, List<Integer> children) {
        NodeSchema node = NodeSchema.buildSkeletonNode(
            joint.name(),
            joint.rotation(),
            joint.translation(),
            joint.scale(),
            children.isEmpty() ? null : children
        );
        nodes.add(node);
    }

    private void buildSkeletonSkin(List<Integer> jointIndices) {
        int inverseBindMatrices = buildAccessor(jointIndices.size() * 16, BufferType.InverseBind, null, null);
        skins.add(new SkinSchema(
            skeletonNode,
            jointIndices,
            inverseBindMatrices
        ));
    }

    private void buildAnimations() {
        if (animation == null) {
            return;
        }

        List<AnimationChannelSchema> channels = new ArrayList<>();
        List<AnimationSamplerSchema> samplers = new ArrayList<>();

        for (int i = 0; i < skeleton.joints().size(); i++) {
            int numRotations = countNonNull(animation.rotations()[i]);
            if (numRotations != 0) {
                FloatBuffer buffer = buildKeyFrameBuffer(animation.rotations()[i]);
                int rotationInput = buildAccessor(numRotations, BufferType.KeyFrame, new float[]{min(buffer)}, new float[]{max(buffer)});
                int rotationOutput = buildAccessor(numRotations * 4, BufferType.Rotation, null, null);
                samplers.add(new AnimationSamplerSchema(rotationInput, rotationOutput));
                AnimationChannelTargetSchema rotationChannelTargetSchema = new AnimationChannelTargetSchema(skeletonNode + i, "rotation");
                channels.add(new AnimationChannelSchema(samplers.size() - 1, rotationChannelTargetSchema));
            }

            int numScales = countNonNull(animation.scales()[i]);
            if (numScales != 0) {
                FloatBuffer buffer = buildKeyFrameBuffer(animation.scales()[i]);
                int scaleInput = buildAccessor(numScales, BufferType.KeyFrame, new float[]{min(buffer)}, new float[]{max(buffer)});
                int scaleOutput = buildAccessor(numScales * 3, BufferType.ScaleTranslation, null, null);
                samplers.add(new AnimationSamplerSchema(scaleInput, scaleOutput));
                AnimationChannelTargetSchema scaleChannelTargetSchema = new AnimationChannelTargetSchema(skeletonNode + i, "scale");
                channels.add(new AnimationChannelSchema(samplers.size() - 1, scaleChannelTargetSchema));
            }

            int numTranslations = countNonNull(animation.translations()[i]);
            if (numTranslations != 0) {
                FloatBuffer buffer = buildKeyFrameBuffer(animation.translations()[i]);
                int translationInput = buildAccessor(numTranslations, BufferType.KeyFrame, new float[]{min(buffer)}, new float[]{max(buffer)});
                int translationOutput = buildAccessor(numTranslations * 3, BufferType.ScaleTranslation, null, null);
                samplers.add(new AnimationSamplerSchema(translationInput, translationOutput));
                AnimationChannelTargetSchema translationChannelTargetSchema = new AnimationChannelTargetSchema(skeletonNode + i, "translation");
                channels.add(new AnimationChannelSchema(samplers.size() - 1, translationChannelTargetSchema));
            }
        }

        animations.add(new AnimationSchema(animation.name(), channels, samplers));
    }

    private float min(FloatBuffer buffer) {
        float min = Float.POSITIVE_INFINITY;
        for (int i = 0; i < buffer.capacity(); i++) {
            min = Math.min(min, buffer.get(i));
        }
        return min;
    }

    private float max(FloatBuffer buffer) {
        float max = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < buffer.capacity(); i++) {
            max = Math.max(max, buffer.get(i));
        }
        return max;
    }

    private <T> int countNonNull(T[] array) {
        return (int) Arrays.stream(array)
            .filter(Objects::nonNull)
            .count();
    }


    private int alignedLength(int length) {
        return (length + 3) & ~3;
    }

    private void align(int length, byte pad) throws IOException {
        byte[] padding = new byte[alignedLength(length) - length];
        Arrays.fill(padding, pad);
        channel.write(ByteBuffer.wrap(padding));
    }

    private void writeBuffers() throws IOException {
        for (Mesh mesh : sourceMeshes) {
            writeBuffer(mesh.positions());
            writeBuffer(mesh.normals());
            writeBuffer(mesh.tangents());
            if (mesh.texCoords() != null) {
                writeBuffer(mesh.texCoords());
            }
            if (skeleton != null) {
                writeBuffer(mesh.joints());
                writeBuffer(mesh.weights());
            } else {
                if (mesh.colors() != null) {
                    writeBuffer(mesh.colors());
                }
            }
            writeBuffer(mesh.indices());
        }

        // Write inverse bind matrices
        if (skeleton != null) {
            List<Bone> joints = skeleton.joints();
            FloatBuffer buffer = FloatBuffer.allocate(joints.size() * 16);
            for (Bone joint : joints) {
                buffer.put(joint.inverseBasePose().toArray());
            }
            writeBuffer(buffer.flip());

            if (animation != null) {
                for (int i = 0; i < joints.size(); i++) {
                    writeBuffer(buildKeyFrameBuffer(animation.rotations()[i]));
                    writeBuffer(buildRotationBuffer(animation.rotations()[i]));
                    writeBuffer(buildKeyFrameBuffer(animation.scales()[i]));
                    writeBuffer(buildScaleTranslationBuffer(animation.scales()[i]));
                    writeBuffer(buildKeyFrameBuffer(animation.translations()[i]));
                    writeBuffer(buildScaleTranslationBuffer(animation.translations()[i]));
                }
            }
        }
    }

    private <T> FloatBuffer buildKeyFrameBuffer(T[] vectors) {
        FloatBuffer buffer = FloatBuffer.allocate(countNonNull(vectors));
        for (int i = 0; i < vectors.length; i++) {
            if (vectors[i] != null) {
                buffer.put((float) i / (float) animation.frameRate());
            }
        }
        return buffer.flip();
    }

    private FloatBuffer buildRotationBuffer(Quaternion[] vectors) {
        int count = countNonNull(vectors);
        FloatBuffer buffer = FloatBuffer.allocate(count * 4);
        for (Quaternion vector : vectors) {
            if (vector != null) {
                vector.put(buffer);
            }
        }
        return buffer.flip();
    }

    private FloatBuffer buildScaleTranslationBuffer(Vector3[] vectors) {
        int count = countNonNull(vectors);
        FloatBuffer buffer = FloatBuffer.allocate(count * 3);
        for (Vector3 vector : vectors) {
            if (vector != null) {
                vector.put(buffer);
            }
        }
        return buffer.flip();
    }

    private void writeBuffer(FloatBuffer floatBuffer) throws IOException {
        ByteBuffer buffer = ByteBuffer
            .allocate(floatBuffer.capacity() * 4)
            .order(ByteOrder.LITTLE_ENDIAN);

        buffer.asFloatBuffer().put(floatBuffer);

        writeBuffer(buffer);
    }

    private void writeBuffer(ShortBuffer shortBuffer) throws IOException {
        ByteBuffer buffer = ByteBuffer
            .allocate(shortBuffer.capacity() * 2)
            .order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < shortBuffer.capacity(); i += 3) {
            buffer.putShort(shortBuffer.get(i));
            buffer.putShort(shortBuffer.get(i + 2));
            buffer.putShort(shortBuffer.get(i + 1));
        }

        writeBuffer(buffer.flip());
    }

    private void writeBuffer(ByteBuffer buffer) throws IOException {
        channel.write(buffer);
        align(buffer.capacity(), (byte) 0);
    }

    private static Bounds calculateBounds(FloatBuffer vertices) {
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        float maxZ = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < vertices.capacity(); i += 3) {
            float x = vertices.get(i);
            float y = vertices.get(i + 1);
            float z = vertices.get(i + 2);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }
        return new Bounds(new Vector3(minX, minY, minZ), new Vector3(maxX, maxY, maxZ));
    }

}
