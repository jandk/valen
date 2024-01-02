package be.twofold.valen.export.gltf;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.export.gltf.gson.*;
import be.twofold.valen.export.gltf.model.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public final class GltfWriter {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(AccessorComponentType.class, new AccessorComponentTypeTypeAdapter())
        .registerTypeAdapter(AccessorType.class, new AccessorTypeTypeAdapter())
        .registerTypeAdapter(BufferViewTarget.class, new BufferViewTargetTypeAdapter().nullSafe())
        .registerTypeAdapter(Quaternion.class, new QuaternionTypeAdapter().nullSafe())
        .registerTypeAdapter(Vector2.class, new be.twofold.valen.writer.gltf.gson.Vector2TypeAdapter())
        .registerTypeAdapter(Vector3.class, new Vector3TypeAdapter().nullSafe())
        .registerTypeAdapter(Vector4.class, new Vector4TypeAdapter())
        .create();

    private final WritableByteChannel channel;
    private final List<Mesh> sourceMeshes;
    private final Skeleton skeleton;
    private final Animation animation;

    private final List<AccessorSchema> accessors = new ArrayList<>();
    private final List<BufferViewSchema> bufferViews = new ArrayList<>();
    private final List<BufferSchema> buffers = new ArrayList<>();
    private final List<MeshSchema> meshes = new ArrayList<>();
    private final List<NodeSchema> nodes = new ArrayList<>();
    private final List<SceneSchema> scenes = new ArrayList<>();
    private final List<SkinSchema> skins = new ArrayList<>();
    private final List<AnimationSchema> animations = new ArrayList<>();

    private final List<ByteBuffer> writable = new ArrayList<>();
    private int bufferOffset;
    private int skeletonNode;

    public GltfWriter(
        WritableByteChannel channel,
        List<Mesh> sourceMeshes,
        Skeleton skeleton,
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
            scenes.getFirst().nodes().add(skeletonNode);
        }
        buildBuffers(); // Only now we know the buffer offset

        try {
            GltfSchema gltf = buildGltf();

            String json = GSON.toJson(gltf);
            byte[] rawJson = json.getBytes(StandardCharsets.UTF_8);
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
        attributes.addProperty("POSITION", buildAccessor(mesh.getBuffer(Semantic.Position).orElseThrow(), Semantic.Position));
        attributes.addProperty("NORMAL", buildAccessor(mesh.getBuffer(Semantic.Normal).orElseThrow(), Semantic.Normal));
        attributes.addProperty("TANGENT", buildAccessor(mesh.getBuffer(Semantic.Tangent).orElseThrow(), Semantic.Tangent));

        mesh.getBuffer(Semantic.TexCoord).ifPresent(buffer -> attributes.addProperty("TEXCOORD_0", buildAccessor(buffer, Semantic.TexCoord)));
        if (skeleton != null) {
            var joints = mesh.getBuffer(Semantic.Joints).orElseThrow();
            var weights = mesh.getBuffer(Semantic.Weights).orElseThrow();
            fixJointsWithEmptyWeights(joints.buffer(), weights.buffer());
            attributes.addProperty("JOINTS_0", buildAccessor(joints, Semantic.Joints));
            attributes.addProperty("WEIGHTS_0", buildAccessor(weights, Semantic.Weights));
        } else {
            mesh.getBuffer(Semantic.Color).ifPresent(buffer -> attributes.addProperty("COLOR_0", buildAccessor(buffer, Semantic.Color)));
        }

        return new PrimitiveSchema(
            attributes,
            buildAccessor(mesh.faceBuffer(), null)
        );
    }

    private void fixJointsWithEmptyWeights(ByteBuffer bones, ByteBuffer weights) {
        byte[] ja = bones.array();
        byte[] wa = weights.array();
        for (int i = 0; i < ja.length; i++) {
            if (wa[i] == 0) {
                ja[i] = 0;
            }
        }
    }

    private int buildAccessor(VertexBuffer buffer, Semantic semantic) {
        var target = semantic == null
            ? BufferViewTarget.ELEMENT_ARRAY_BUFFER
            : BufferViewTarget.ARRAY_BUFFER;

        int bufferView = createBufferView(buffer.length(), target);
        return buildAccessor(bufferView, buffer, semantic == Semantic.Position);
    }

    private int buildAccessor(int bufferView, VertexBuffer buffer, boolean calculateBounds) {
        var bounds = calculateBounds
            ? Bounds.calculate(buffer.buffer().asFloatBuffer())
            : null;

        return createAccessor(bufferView, buffer, bounds);
    }

    private int createAccessor(int bufferView, VertexBuffer buffer, Bounds bounds) {
        var accessor = new AccessorSchema(
            bufferView,
            AccessorComponentType.from(buffer.componentType()),
            buffer.count(),
            AccessorType.from(buffer.elementType()),
            bounds != null ? bounds.min().toArray() : null,
            bounds != null ? bounds.max().toArray() : null,
            buffer.normalized() ? true : null
        );
        accessors.add(accessor);
        return accessors.size() - 1;
    }

    private int createBufferView(int length, BufferViewTarget target) {
        var bufferView = new BufferViewSchema(0, bufferOffset, length, target);
        bufferViews.add(bufferView);

        // Round up offset to a multiple of 4
        bufferOffset = alignedLength(bufferOffset + length);
        return bufferViews.size() - 1;
    }

    private int buildAccessor(int capacity, BufferType bufferType, float[] min, float[] max) {
        int bufferView = createBufferView(capacity * bufferType.getComponentType().getSize(), null);
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
            null
        );
        accessors.add(accessor);
        return accessors.size() - 1;
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
        List<Bone> bones = skeleton.bones();

        // Calculate the parent-child relationships
        Map<Integer, List<Integer>> children = new HashMap<>();
        for (int i = 0; i < bones.size(); i++) {
            Bone joint = bones.get(i);
            children
                .computeIfAbsent(joint.parent(), __ -> new ArrayList<>())
                .add(offset + i);
        }

        // Build the skeleton
        List<Integer> jointIndices = new ArrayList<>();
        for (int i = 0; i < bones.size(); i++) {
            if (bones.get(i).parent() == -1) {
                skeletonNode = offset + i;
            }
            jointIndices.add(offset + i);
            buildSkeletonJoint(bones.get(i), children.getOrDefault(i, List.of()));
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

        for (int i = 0; i < skeleton.bones().size(); i++) {
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
            writeBuffer(mesh.getBuffer(Semantic.Position).orElseThrow().buffer());
            writeBuffer(mesh.getBuffer(Semantic.Normal).orElseThrow().buffer());
            writeBuffer(mesh.getBuffer(Semantic.Tangent).orElseThrow().buffer());
            mesh.getBuffer(Semantic.TexCoord).ifPresent(buffer -> writeBuffer(buffer.buffer()));
            if (skeleton != null) {
                writeBuffer(mesh.getBuffer(Semantic.Joints).orElseThrow().buffer());
                writeBuffer(mesh.getBuffer(Semantic.Weights).orElseThrow().buffer());
            } else {
                mesh.getBuffer(Semantic.Color).ifPresent(buffer -> writeBuffer(buffer.buffer()));
            }
            writeBuffer(mesh.faceBuffer().buffer());
        }

        // Write inverse bind matrices
        if (skeleton != null) {
            List<Bone> bones = skeleton.bones();
            FloatBuffer buffer = FloatBuffer.allocate(bones.size() * 16);
            for (Bone joint : bones) {
                buffer.put(joint.inverseBasePose().toArray());
            }
            writeBuffer(buffer.flip());

            if (animation != null) {
                for (int i = 0; i < bones.size(); i++) {
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

    private void writeBuffer(FloatBuffer floatBuffer) {
        ByteBuffer buffer = ByteBuffer
            .allocate(floatBuffer.capacity() * 4)
            .order(ByteOrder.LITTLE_ENDIAN);

        buffer.asFloatBuffer().put(floatBuffer);

        writeBuffer(buffer);
    }

    private void writeBuffer(ByteBuffer buffer) {
        try {
            channel.write(buffer);
            align(buffer.capacity(), (byte) 0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
