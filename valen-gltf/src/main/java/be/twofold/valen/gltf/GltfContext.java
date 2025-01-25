package be.twofold.valen.gltf;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.accessor.*;
import be.twofold.valen.gltf.model.animation.*;
import be.twofold.valen.gltf.model.asset.*;
import be.twofold.valen.gltf.model.buffer.*;
import be.twofold.valen.gltf.model.bufferview.*;
import be.twofold.valen.gltf.model.camera.*;
import be.twofold.valen.gltf.model.extension.*;
import be.twofold.valen.gltf.model.image.*;
import be.twofold.valen.gltf.model.material.*;
import be.twofold.valen.gltf.model.mesh.*;
import be.twofold.valen.gltf.model.node.*;
import be.twofold.valen.gltf.model.sampler.*;
import be.twofold.valen.gltf.model.scene.*;
import be.twofold.valen.gltf.model.skin.*;
import be.twofold.valen.gltf.model.texture.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

public class GltfContext {
    private final Map<String, Extension> extensions = new HashMap<>();
    private final Set<String> extensionsRequired = new HashSet<>();
    private final Set<String> extensionsUsed = new HashSet<>();
    private final List<AccessorSchema> accessors = new ArrayList<>();
    private final List<AnimationSchema> animations = new ArrayList<>();
    private final List<BufferSchema> buffers = new ArrayList<>();
    private final List<BufferViewSchema> bufferViews = new ArrayList<>();
    private final List<CameraSchema> cameras = new ArrayList<>();
    private final List<ImageSchema> images = new ArrayList<>();
    private final List<MaterialSchema> materials = new ArrayList<>();
    private final List<MeshSchema> meshes = new ArrayList<>();
    private final List<NodeSchema> nodes = new ArrayList<>();
    private final List<SamplerSchema> samplers = new ArrayList<>();
    private final List<SceneSchema> scenes = new ArrayList<>();
    private final List<SkinSchema> skins = new ArrayList<>();
    private final List<TextureSchema> textures = new ArrayList<>();

    private final List<NodeID> sceneNodes = new ArrayList<>();

    private final JsonWriter jsonWriter;

    public GltfContext(OutputStream binOutput, Path imagePath) {
        jsonWriter = JsonWriter.create();
        this.binOutput = binOutput;
        this.imagePath = imagePath;
    }

    // region Adders

    public void addExtension(Extension extension) {
        extensions.put(extension.getName(), extension);
        registerExtension(extension);
    }

    public AccessorID addAccessor(AccessorSchema accessor) {
        accessors.add(accessor);
        registerExtensions(accessor);
        return AccessorID.of(accessors.size() - 1);
    }

    public void addAnimation(AnimationSchema animation) {
        animations.add(animation);
        registerExtensions(animation);
    }

    public BufferID addBuffer(BufferSchema buffer) {
        buffers.add(buffer);
        registerExtensions(buffer);
        return BufferID.of(buffers.size() - 1);
    }

    public BufferViewID addBufferView(BufferViewSchema bufferView) {
        bufferViews.add(bufferView);
        registerExtensions(bufferView);
        return BufferViewID.of(bufferViews.size() - 1);
    }

    public CameraID addCamera(CameraSchema camera) {
        cameras.add(camera);
        registerExtensions(camera);
        return CameraID.of(cameras.size() - 1);
    }

    public ImageID addImage(ImageSchema image) {
        images.add(image);
        registerExtensions(image);
        return ImageID.of(images.size() - 1);
    }

    public MaterialID addMaterial(MaterialSchema material) {
        materials.add(material);
        registerExtensions(material);
        return MaterialID.of(materials.size() - 1);
    }

    public MeshID addMesh(MeshSchema mesh) {
        meshes.add(mesh);
        registerExtensions(mesh);
        return MeshID.of(meshes.size() - 1);
    }

    public NodeID addNode(NodeSchema node) {
        nodes.add(node);
        registerExtensions(node);
        return NodeID.of(nodes.size() - 1);
    }

    public SamplerID addSampler(SamplerSchema sampler) {
        samplers.add(sampler);
        registerExtensions(sampler);
        return SamplerID.of(samplers.size() - 1);
    }

    public SceneID addScene(SceneSchema scene) {
        scenes.add(scene);
        registerExtensions(scene);
        return SceneID.of(scenes.size() - 1);
    }

    public SkinID addSkin(SkinSchema skin) {
        skins.add(skin);
        registerExtensions(skin);
        skin.getSkeleton().ifPresent(sceneNodes::add);
        return SkinID.of(skins.size() - 1);
    }

    public TextureID addTexture(TextureSchema texture) {
        textures.add(texture);
        registerExtensions(texture);
        return TextureID.of(textures.size() - 1);
    }

    public void registerExtension(Extension extension) {
        extensionsUsed.add(extension.getName());
        if (extension.isRequired()) {
            extensionsRequired.add(extension.getName());
        }
    }

    private void registerExtensions(GltfProperty property) {
        property.getExtensions().values()
            .forEach(this::registerExtension);
    }

    // endregion

    // region Buffers

    private final List<Buffer> buffersToWrite = new ArrayList<>();
    private final OutputStream binOutput;
    private final Path imagePath;
    private int buffersLength;

    public ImageID createImage(ByteBuffer buffer, String name, String filename, ImageMimeType mimeType) throws IOException {
        var builder = ImmutableImage.builder()
            .name(name)
            .mimeType(mimeType);

        if (imagePath != null) {
            var outPath = imagePath.resolve(filename);
            Files.write(outPath, buffer.array());
            return addImage(builder
                .uri(outPath.toUri())
                .build());
        } else {
            var bufferViewID = createBufferView(buffer, null);

            return addImage(builder
                .bufferView(bufferViewID)
                .build());
        }
    }

    public BufferViewID createBufferView(Buffer buffer, BufferViewTarget target) throws IOException {
        int offset = buffersLength;
        int length = byteSize(buffer);

        if (binOutput != null) {
            binOutput.write(toByteArray(buffer));
            GltfUtils.align(binOutput, length, (byte) 0);
        } else {
            buffersToWrite.add(buffer);
        }

        buffersLength = GltfUtils.alignedLength(buffersLength + length);

        var bufferView = ImmutableBufferView.builder()
            .buffer(BufferID.ZERO)
            .byteOffset(offset)
            .byteLength(length)
            .target(Optional.ofNullable(target))
            .build();

        return addBufferView(bufferView);
    }

    public void finalizeBuffers(URI uri) {
        var buffer = ImmutableBuffer.builder()
            .byteLength(buffersLength)
            .uri(Optional.ofNullable(uri))
            .build();

        addBuffer(buffer);
    }

    public int buffersLength() {
        return buffersLength;
    }

    public void writeBuffers(OutputStream out) throws IOException {
        for (var buffer : buffersToWrite) {
            var array = toByteArray(buffer);
            out.write(array);
            GltfUtils.align(out, array.length, (byte) 0);
        }
    }

    private int byteSize(Buffer buffer) {
        return switch (buffer) {
            case ByteBuffer bb -> bb.limit();
            case ShortBuffer sb -> sb.limit() * Short.BYTES;
            case IntBuffer ib -> ib.limit() * Integer.BYTES;
            case LongBuffer lb -> lb.limit() * Long.BYTES;
            case FloatBuffer fb -> fb.limit() * Float.BYTES;
            case DoubleBuffer db -> db.limit() * Double.BYTES;
            case CharBuffer cb -> cb.limit() * Character.BYTES;
        };
    }

    private byte[] toByteArray(Buffer buffer) {
        buffer.rewind();
        return switch (buffer) {
            case ByteBuffer bb -> bb.array();
            case ShortBuffer sb -> allocateAndApply(buffer, bb -> bb.asShortBuffer().put(sb));
            case IntBuffer ib -> allocateAndApply(buffer, bb -> bb.asIntBuffer().put(ib));
            case LongBuffer lb -> allocateAndApply(buffer, bb -> bb.asLongBuffer().put(lb));
            case FloatBuffer fb -> allocateAndApply(buffer, bb -> bb.asFloatBuffer().put(fb));
            case DoubleBuffer db -> allocateAndApply(buffer, bb -> bb.asDoubleBuffer().put(db));
            case CharBuffer cb -> allocateAndApply(buffer, bb -> bb.asCharBuffer().put(cb));
        };
    }

    private byte[] allocateAndApply(Buffer buffer, Consumer<ByteBuffer> consumer) {
        var byteBuffer = ByteBuffer
            .allocate(byteSize(buffer))
            .order(ByteOrder.LITTLE_ENDIAN);
        consumer.accept(byteBuffer);
        return byteBuffer.array();
    }

    // endregion

    public void addScene(List<NodeID> nodes) {
        var scene = ImmutableScene.builder()
            .addAllNodes(nodes)
            .addAllNodes(sceneNodes)
            .build();

        addScene(scene);
    }

    public GltfSchema buildGltf() {
        var asset = ImmutableAsset.builder()
            .version("2.0")
            .generator("Valen")
            .build();

        return ImmutableGltf.builder()
            .asset(asset)
            .extensionsRequired(extensionsRequired)
            .extensionsUsed(extensionsUsed)
            .extensions(extensions)
            .accessors(accessors)
            .animations(animations)
            .buffers(buffers)
            .bufferViews(bufferViews)
            .cameras(cameras)
            .images(images)
            .materials(materials)
            .meshes(meshes)
            .nodes(nodes)
            .samplers(samplers)
            .scenes(scenes)
            .skins(skins)
            .textures(textures)
            .build();
    }

    public NodeID nextNodeId() {
        return NodeID.of(nodes.size());
    }

    byte[] toRawJson() throws IOException {
        try (var writer = new StringWriter()) {
            jsonWriter.writeJson(buildGltf(), writer, false);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        }
    }
}
