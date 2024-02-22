package be.twofold.valen.export.gltf;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.export.gltf.gson.*;
import be.twofold.valen.export.gltf.model.*;
import be.twofold.valen.export.gltf.model.Extension;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public final class GltfWriter implements GltfContext {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeHierarchyAdapter(AbstractId.class, new AbstractIdTypeAdapter().nullSafe())
        .registerTypeHierarchyAdapter(Collection.class, new CollectionSerializer())
        .registerTypeHierarchyAdapter(Map.class, new MapSerializer())
        .registerTypeAdapter(AccessorComponentType.class, new AccessorComponentTypeTypeAdapter())
        .registerTypeAdapter(AccessorType.class, new AccessorTypeTypeAdapter())
        .registerTypeAdapter(AnimationChannelTargetPath.class, new AnimationChannelTargetPathTypeAdapter())
        .registerTypeAdapter(AnimationSamplerInterpolation.class, new AnimationSamplerInterpolationTypeAdapter())
        .registerTypeAdapter(BufferViewTarget.class, new BufferViewTargetTypeAdapter().nullSafe())
        .registerTypeAdapter(Matrix4.class, new Matrix4TypeAdapter().nullSafe())
        .registerTypeAdapter(Quaternion.class, new QuaternionTypeAdapter().nullSafe())
        .registerTypeAdapter(Vector2.class, new Vector2TypeAdapter().nullSafe())
        .registerTypeAdapter(Vector3.class, new Vector3TypeAdapter().nullSafe())
        .registerTypeAdapter(Vector4.class, new Vector4TypeAdapter().nullSafe())
        .create();

    private final WritableByteChannel channel;

    private final List<AccessorSchema> accessors = new ArrayList<>();
    private final List<BufferViewSchema> bufferViews = new ArrayList<>();
    private final List<BufferSchema> buffers = new ArrayList<>();
    private final List<MeshSchema> meshes = new ArrayList<>();
    private final List<NodeSchema> nodes = new ArrayList<>();
    private final List<SceneSchema> scenes = new ArrayList<>();
    private final List<SkinSchema> skins = new ArrayList<>();
    private final List<AnimationSchema> animations = new ArrayList<>();
    private final List<String> usedExtensions = new ArrayList<>();
    private final List<String> requiredExtensions = new ArrayList<>();
    private final Map<String, Extension> extensions = new HashMap<>();

    private final List<Buffer> writable = new ArrayList<>();
    private int bufferLength;

    private final GltfModelMapper modelMapper = new GltfModelMapper(this);
    private final GltfSkeletonMapper skeletonMapper = new GltfSkeletonMapper(this);
    private final GltfAnimationMapper animationMapper = new GltfAnimationMapper(this);

    public GltfWriter(WritableByteChannel channel) {
        this.channel = channel;
    }

    public void write() {
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
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    public Map<String, Extension> getExtensions() {
        return extensions;
    }

    public void addExtension(String name, Extension extension, boolean required) {
        usedExtensions.add(name);
        if (required) {
            requiredExtensions.add(name);
        }
        extensions.put(name, extension);
    }

    public SceneSchema addScene(List<NodeId> nodes) {
        var scene = SceneSchema.builder()
            .addAllNodes(nodes)
            .build();
        scenes.add(scene);
        return scene;
    }

    public MeshSchema convertMesh(Model model) {
        return modelMapper.map(model);
    }

    public SkinSchema convertSkeleton(Skeleton skeleton) {
        return skeletonMapper.map(skeleton, nodes.size());
    }

    public MeshId addMesh(Model model) {
        meshes.add(convertMesh(model));
        return MeshId.of(meshes.size() - 1);
    }

    public AbstractMap.SimpleEntry<NodeId, SkinId> addSkin(Skeleton skeleton) {
        var skeletonRootNodeId = NodeId.of(nodes.size());
        var skin = convertSkeleton(skeleton);
        skins.add(skin);
        return new AbstractMap.SimpleEntry<>(skeletonRootNodeId, SkinId.of(skins.size() - 1));
    }

    private GltfSchema buildGltf() {
        var asset = AssetSchema.builder()
            .generator("Valen")
            .version("2.0")
            .build();

        return GltfSchema.builder()
            .asset(asset)
            .accessors(accessors)
            .animations(animations)
            .bufferViews(bufferViews)
            .buffers(buffers)
            .meshes(meshes)
            .nodes(nodes)
            .scenes(scenes)
            .skins(skins)
            .extensionsUsed(usedExtensions)
            .extensionsRequired(requiredExtensions)
            .extensions(extensions)
            .build();
    }


    private void buildBuffers() {
        BufferSchema buffer = BufferSchema.builder()
            .byteLength(bufferLength)
            .build();
        buffers.add(buffer);
    }

    @Override
    public AccessorId addAccessor(AccessorSchema accessor) {
        accessors.add(accessor);
        return AccessorId.of(accessors.size() - 1);
    }

    @Override
    public NodeId addNode(NodeSchema node) {
        nodes.add(node);
        return NodeId.of(nodes.size() - 1);
    }

    @Override
    public BufferViewId createBufferView(Buffer buffer, int length, BufferViewTarget target) {
        writable.add(buffer);
        return createBufferView(length, target);
    }

    private BufferViewId createBufferView(int length, BufferViewTarget target) {
        var bufferView = BufferViewSchema.builder()
            .buffer(BufferId.of(0))
            .byteOffset(bufferLength)
            .byteLength(length)
            .target(Optional.ofNullable(target))
            .build();
        bufferViews.add(bufferView);

        // Round up offset to a multiple of 4
        bufferLength = alignedLength(bufferLength + length);
        return BufferViewId.of(bufferViews.size() - 1);
    }


    private int alignedLength(int length) {
        return (length + 3) & ~3;
    }

    private void align(int length, byte pad) throws IOException {
        byte[] padding = new byte[alignedLength(length) - length];
        Arrays.fill(padding, pad);
        channel.write(ByteBuffer.wrap(padding));
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
