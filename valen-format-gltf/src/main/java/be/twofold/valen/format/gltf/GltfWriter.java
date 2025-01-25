package be.twofold.valen.format.gltf;

import be.twofold.valen.format.gltf.glb.*;
import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.accessor.*;
import be.twofold.valen.format.gltf.model.animation.*;
import be.twofold.valen.format.gltf.model.asset.*;
import be.twofold.valen.format.gltf.model.buffer.*;
import be.twofold.valen.format.gltf.model.bufferview.*;
import be.twofold.valen.format.gltf.model.camera.*;
import be.twofold.valen.format.gltf.model.extension.*;
import be.twofold.valen.format.gltf.model.image.*;
import be.twofold.valen.format.gltf.model.material.*;
import be.twofold.valen.format.gltf.model.mesh.*;
import be.twofold.valen.format.gltf.model.node.*;
import be.twofold.valen.format.gltf.model.sampler.*;
import be.twofold.valen.format.gltf.model.scene.*;
import be.twofold.valen.format.gltf.model.skin.*;
import be.twofold.valen.format.gltf.model.texture.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

public final class GltfWriter implements Closeable, GltfContext {
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
    private final JsonWriter jsonWriter = JsonWriter.create();

    // Buffer management
    private final List<Buffer> buffersToWrite = new ArrayList<>();
    private final OutputStream gltfOut;
    private final OutputStream binOut;
    private final Path gltfPath;
    private final Path binPath;
    private final Path imagePath;
    private int buffersLength;

    private GltfWriter(Path gltfPath, Path binPath, Path imagePath) throws IOException {
        this.gltfOut = Files.newOutputStream(gltfPath);
        this.binOut = binPath == null ? null : Files.newOutputStream(binPath);

        this.gltfPath = gltfPath;
        this.binPath = binPath;
        this.imagePath = imagePath;
    }

    public static GltfWriter createGlbWriter(Path gltfPath) throws IOException {
        return new GltfWriter(gltfPath, null, null);
    }

    public static GltfWriter createSplitWriter(Path gltfPath, Path binPath, Path imagePath) throws IOException {
        return new GltfWriter(gltfPath, binPath, imagePath);
    }

    public void addScene(List<NodeID> nodes) {
        var sceneNodes = skins.stream()
            .flatMap(skinSchema -> skinSchema.getSkeleton().stream())
            .toList();

        var scene = ImmutableScene.builder()
            .addAllNodes(nodes)
            .addAllNodes(sceneNodes)
            .build();

        addScene(scene);
    }

    // region GltfContext

    @Override
    public void addExtension(Extension extension) {
        extensions.put(extension.getName(), extension);
        registerExtension(extension);
    }

    @Override
    public AccessorID addAccessor(AccessorSchema accessor) {
        accessors.add(accessor);
        registerExtensions(accessor);
        return AccessorID.of(accessors.size() - 1);
    }

    @Override
    public void addAnimation(AnimationSchema animation) {
        animations.add(animation);
        registerExtensions(animation);
    }

    @Override
    public BufferID addBuffer(BufferSchema buffer) {
        buffers.add(buffer);
        registerExtensions(buffer);
        return BufferID.of(buffers.size() - 1);
    }

    @Override
    public BufferViewID addBufferView(BufferViewSchema bufferView) {
        bufferViews.add(bufferView);
        registerExtensions(bufferView);
        return BufferViewID.of(bufferViews.size() - 1);
    }

    @Override
    public CameraID addCamera(CameraSchema camera) {
        cameras.add(camera);
        registerExtensions(camera);
        return CameraID.of(cameras.size() - 1);
    }

    @Override
    public ImageID addImage(ImageSchema image) {
        images.add(image);
        registerExtensions(image);
        return ImageID.of(images.size() - 1);
    }

    @Override
    public MaterialID addMaterial(MaterialSchema material) {
        materials.add(material);
        registerExtensions(material);
        return MaterialID.of(materials.size() - 1);
    }

    @Override
    public MeshID addMesh(MeshSchema mesh) {
        meshes.add(mesh);
        registerExtensions(mesh);
        return MeshID.of(meshes.size() - 1);
    }

    @Override
    public NodeID addNode(NodeSchema node) {
        nodes.add(node);
        registerExtensions(node);
        return NodeID.of(nodes.size() - 1);
    }

    @Override
    public SamplerID addSampler(SamplerSchema sampler) {
        samplers.add(sampler);
        registerExtensions(sampler);
        return SamplerID.of(samplers.size() - 1);
    }

    @Override
    public SceneID addScene(SceneSchema scene) {
        scenes.add(scene);
        registerExtensions(scene);
        return SceneID.of(scenes.size() - 1);
    }

    @Override
    public SkinID addSkin(SkinSchema skin) {
        skins.add(skin);
        registerExtensions(skin);
        return SkinID.of(skins.size() - 1);
    }

    @Override
    public TextureID addTexture(TextureSchema texture) {
        textures.add(texture);
        registerExtensions(texture);
        return TextureID.of(textures.size() - 1);
    }

    @Override
    public ImageID createImage(ByteBuffer buffer, String name, String filename, ImageMimeType mimeType) throws IOException {
        var builder = ImmutableImage.builder()
            .name(name)
            .mimeType(mimeType);

        if (imagePath != null) {
            var extension = switch (mimeType) {
                case IMAGE_JPEG -> ".jpg";
                case IMAGE_PNG -> ".png";
            };
            var outPath = imagePath.resolve(filename + extension);
            Files.write(outPath, buffer.array());
            return addImage(builder
                .uri(URI.create(gltfPath.getParent().relativize(outPath).toString().replace('\\', '/')))
                .build());
        } else {
            var bufferViewID = createBufferView(buffer, null);

            return addImage(builder
                .bufferView(bufferViewID)
                .build());
        }
    }

    @Override
    public BufferViewID createBufferView(Buffer buffer, BufferViewTarget target) throws IOException {
        int offset = buffersLength;
        int length = byteSize(buffer);

        if (binOut != null) {
            binOut.write(toByteArray(buffer));
            alignTo4(binOut, length, (byte) 0);
        } else {
            buffersToWrite.add(buffer);
        }

        buffersLength = alignedLength(buffersLength + length);

        var bufferView = ImmutableBufferView.builder()
            .buffer(BufferID.ZERO)
            .byteOffset(offset)
            .byteLength(length)
            .target(Optional.ofNullable(target))
            .build();

        return addBufferView(bufferView);
    }

    @Override
    public NodeID nextNodeId() {
        return NodeID.of(nodes.size());
    }

    // endregion

    // region Finishing

    private void finish() throws IOException {
        var relativeBinPath = binPath != null
            ? gltfPath.getParent().relativize(binPath)
            : null;

        var buffer = ImmutableBuffer.builder()
            .byteLength(buffersLength)
            .uri(Optional.ofNullable(relativeBinPath).map(path -> URI.create(path.toString().replace('\\', '/'))))
            .build();

        buffers.clear();
        addBuffer(buffer);

        if (binPath != null) {
            try (var writer = new OutputStreamWriter(gltfOut, StandardCharsets.UTF_8)) {
                jsonWriter.writeJson(buildGltf(), writer, true);
            }
        } else {
            finishGlb();
        }
    }

    private void finishGlb() throws IOException {
        var writer = new StringWriter();
        jsonWriter.writeJson(buildGltf(), writer, false);
        var rawJson = writer.toString().getBytes(StandardCharsets.UTF_8);

        var jsonSize = alignedLength(rawJson.length);
        var binSize = buffersLength;

        var totalSize = GlbHeader.BYTES + GlbChunkHeader.BYTES + jsonSize + GlbChunkHeader.BYTES + binSize;

        gltfOut.write(new GlbHeader(totalSize).toBuffer().array());
        gltfOut.write(new GlbChunkHeader(jsonSize, GlbChunkType.JSON).toBuffer().array());
        gltfOut.write(rawJson);
        alignTo4(gltfOut, rawJson.length, (byte) ' ');
        gltfOut.write(new GlbChunkHeader(binSize, GlbChunkType.BIN).toBuffer().array());

        for (var buffer : buffersToWrite) {
            var array = toByteArray(buffer);
            gltfOut.write(array);
            alignTo4(gltfOut, array.length, (byte) 0);
        }
    }

    private GltfSchema buildGltf() {
        var asset = ImmutableAsset.builder()
            .version("2.0")
            .generator("Valen")
            .build();

        return ImmutableGltf.builder()
            .asset(asset)
            .extensions(extensions)
            .extensionsRequired(extensionsRequired)
            .extensionsUsed(extensionsUsed)
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

    // endregion

    // region Helper Methods

    private int alignedLength(int length) {
        return (length + 3) & ~3;
    }

    private void alignTo4(OutputStream output, int length, byte pad) throws IOException {
        int padLength = alignedLength(length) - length;
        for (int i = 0; i < padLength; i++) {
            output.write(pad);
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

    private void registerExtensions(GltfProperty property) {
        property.getExtensions().values()
            .forEach(this::registerExtension);
    }

    private void registerExtension(Extension extension) {
        extensionsUsed.add(extension.getName());
        if (extension.isRequired()) {
            extensionsRequired.add(extension.getName());
        }
    }

    // endregion

    @Override
    public void close() throws IOException {
        finish();

        gltfOut.close();
        if (binOut != null) {
            binOut.close();
        }
    }
}
