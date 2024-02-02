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
    private final List<Integer> meshNodes = new ArrayList<>();

    private final List<Buffer> writable = new ArrayList<>();
    private int bufferLength;
    private int skeletonNode;

    private final GltfModelMapper modelMapper = new GltfModelMapper(this);
    private final GltfSkeletonMapper skeletonMapper = new GltfSkeletonMapper(this);
    private final GltfAnimationMapper animationMapper = new GltfAnimationMapper(this);

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
        if (model != null) {
            modelMapper.map(model);
        }
//        buildNodes();
        buildScenes();
        if (skeleton != null) {
            skins.add(skeletonMapper.map(skeleton, nodes.size()));
            if (animation != null) {
                animations.add(animationMapper.map(animation, skeletonNode));
            }

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
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public int addMesh(Model model) {
        meshes.add(modelMapper.map(model));
        return meshes.size() - 1;
    }

    public void addMeshInstance(int mesh, String name, Quaternion rotation, Vector3 translation, Vector3 scale) {
        NodeSchema node = NodeSchema.buildMesh(name, rotation, translation, scale, mesh);
        meshNodes.add(addNode(node));
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

    private void buildNodes() {
        Integer skin = skeleton != null ? 0 : null;
        NodeSchema meshSkin = NodeSchema.buildMeshSkin(0, skin);
        nodes.add(meshSkin);

//        NodeSchema rst = NodeSchema.buildRST(List.of(nodes.size() - 1));
//        nodes.add(rst);
    }

    private void buildScenes() {
        scenes.add(new SceneSchema(meshNodes));
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
