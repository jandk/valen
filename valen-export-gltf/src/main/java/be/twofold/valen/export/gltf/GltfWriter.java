package be.twofold.valen.export.gltf;

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
            .registerTypeAdapterFactory(new GsonAdaptersNodeSchema())
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
    private final JsonObject extensions = new JsonObject();
    private final List<Buffer> writable = new ArrayList<>();
    private int bufferLength;
    private final GltfModelMapper modelMapper = new GltfModelMapper(this);
    private final GltfSkeletonMapper skeletonMapper = new GltfSkeletonMapper(this);
    private final GltfAnimationMapper animationMapper = new GltfAnimationMapper(this);

    public GltfWriter(
            WritableByteChannel channel
    ) {
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

    public void addUsedExtension(String name, boolean required) {
        if (usedExtensions.contains(name)) {
            return;
        }

        usedExtensions.add(name);
        if (required) {
            requiredExtensions.add(name);
        }
    }

    public JsonObject getExtensions() {
        return extensions;
    }

    public SceneSchema addScene() {
        SceneSchema scene = new SceneSchema(new ArrayList<>());
        scenes.add(scene);
        return scene;
    }

    public MeshSchema convertMesh(Model model) {
        return modelMapper.map(model);
    }

    public SkinSchema convertSkeleton(Skeleton skeleton) {
        return skeletonMapper.map(skeleton, nodes.size());
    }

    public int addMesh(Model model) {
        meshes.add(modelMapper.map(model));
        return meshes.size() - 1;
    }

    public int addSkeletalMesh(Model model, Skeleton skeleton, SceneSchema scene) {
        meshes.add(modelMapper.map(model));
        var meshId = meshes.size() - 1;
        var skeletonRootNodeId = nodes.size();
        var skin = skeletonMapper.map(skeleton, nodes.size());
        skins.add(skin);
        var skinId = skins.size() - 1;
        var skinMeshNode = NodeSchema.buildMeshSkin(meshId, skinId);
        nodes.add(skinMeshNode);
        var skinMeshNodeId = nodes.size() - 1;
        scene.addNode(skinMeshNodeId);
        scene.addNode(skeletonRootNodeId);
        return skinMeshNodeId;
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
                skins.isEmpty() ? null : skins,
                animations.isEmpty() ? null : animations,
                usedExtensions,
                requiredExtensions,
                extensions,
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

    private int createBufferView(int length, BufferViewTarget target) {
        var bufferView = new BufferViewSchema(0, bufferLength, length, target);
        bufferViews.add(bufferView);

        // Round up offset to a multiple of 4
        bufferLength = alignedLength(bufferLength + length);
        return bufferViews.size() - 1;
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
