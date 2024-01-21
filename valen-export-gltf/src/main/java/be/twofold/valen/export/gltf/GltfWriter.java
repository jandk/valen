package be.twofold.valen.export.gltf;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.export.gltf.gson.*;
import be.twofold.valen.export.gltf.model.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public final class GltfWriter implements GltfContext {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(AccessorComponentType.class, new AccessorComponentTypeTypeAdapter())
        .registerTypeAdapter(AccessorType.class, new AccessorTypeTypeAdapter())
        .registerTypeAdapter(BufferViewTarget.class, new BufferViewTargetTypeAdapter().nullSafe())
        .registerTypeAdapter(Quaternion.class, new QuaternionTypeAdapter().nullSafe())
        .registerTypeAdapter(Vector2.class, new Vector2TypeAdapter())
        .registerTypeAdapter(Vector3.class, new Vector3TypeAdapter().nullSafe())
        .registerTypeAdapter(Vector4.class, new Vector4TypeAdapter())
        .create();

    private final WritableByteChannel channel;
    private final Model model;
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

    private final List<Buffer> writable = new ArrayList<>();
    private int bufferLength;
    private int skeletonNode;

    private final GltfModelMapper modelMapper = new GltfModelMapper(this);
    private final GltfSkeletonMapper skeletonMapper = new GltfSkeletonMapper(this);

    public GltfWriter(
        WritableByteChannel channel,
        Model model,
        Skeleton skeleton,
        Animation animation
    ) {
        this.channel = channel;
        this.model = model;
        this.skeleton = skeleton;
        this.animation = animation;
    }

    public void write() {
        meshes.add(modelMapper.map(model));
        buildNodes();
        buildScenes();
        if (skeleton != null) {
            skins.add(skeletonMapper.map(skeleton, nodes.size()));
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

            int totalSize = 12 + 8 + alignedJsonLength + 8 + bufferLength;
            channel.write(GlbHeader.of(totalSize).toBuffer());
            channel.write(GlbChunkHeader.of(GlbChunkType.Json, alignedJsonLength).toBuffer());
            channel.write(ByteBuffer.wrap(rawJson));
            align(rawJson.length, (byte) ' ');
            channel.write(GlbChunkHeader.of(GlbChunkType.Bin, bufferLength).toBuffer());
            for (Buffer buffer : writable) {
                writeBuffer(buffer);
            }
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
        BufferSchema buffer = new BufferSchema(bufferLength);
        buffers.add(buffer);
    }

    @Override
    public int addAccessor(AccessorSchema accessor) {
        accessors.add(accessor);
        return accessors.size() - 1;
    }

    @Override
    public int addNode(NodeSchema node) {
        nodes.add(node);
        return nodes.size() - 1;
    }

    @Override
    public int createBufferView(Buffer buffer, int length, BufferViewTarget target) {
        writable.add(buffer);
        return createBufferView(length, target);
    }

    @Override
    public void setSkeletonNode(int skeletonNode) {
        this.skeletonNode = skeletonNode;
    }

    private int createBufferView(int length, BufferViewTarget target) {
        var bufferView = new BufferViewSchema(0, bufferLength, length, target);
        bufferViews.add(bufferView);

        // Round up offset to a multiple of 4
        bufferLength = alignedLength(bufferLength + length);
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

//        NodeSchema rst = NodeSchema.buildRST(List.of(nodes.size() - 1));
//        nodes.add(rst);
    }

    private void buildScenes() {
        List<Integer> nodes = new ArrayList<>();
        nodes.add(0);

        SceneSchema scene = new SceneSchema(nodes);
        scenes.add(scene);
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
        if (skeleton != null && animation != null) {
            for (int i = 0; i < skeleton.bones().size(); i++) {
                writeBuffer(buildKeyFrameBuffer(animation.rotations()[i]));
                writeBuffer(buildRotationBuffer(animation.rotations()[i]));
                writeBuffer(buildKeyFrameBuffer(animation.scales()[i]));
                writeBuffer(buildScaleTranslationBuffer(animation.scales()[i]));
                writeBuffer(buildKeyFrameBuffer(animation.translations()[i]));
                writeBuffer(buildScaleTranslationBuffer(animation.translations()[i]));
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

    private void writeBuffer(Buffer buffer) {
        var byteBuffer = Buffers.toByteBuffer(buffer);
        try {
            channel.write(byteBuffer);
            align(byteBuffer.capacity(), (byte) 0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
