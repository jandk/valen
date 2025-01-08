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

public final class GltfContext {
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

    private final BufferManager bufferManager;
    private final JsonWriter jsonWriter;

    public GltfContext() {
        bufferManager = BufferManager.create();
        jsonWriter = JsonWriter.create();
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

    // region BufferManager proxies

    public ImageID createImage(ByteBuffer buffer, String name, String filename, ImageMimeType mimeType) throws IOException {
        var builder = ImmutableImage.builder()
            .name(name)
            .mimeType(mimeType);

        var offsetLength = bufferManager.addImageBuffer(buffer, filename);
        if (offsetLength != null) {
            var bufferViewID = createBufferView(offsetLength, null);
            return addImage(ImmutableImage.builder()
                .bufferView(bufferViewID)
                .build());
        }

        return addImage(builder
            .uri(Path.of(filename).toUri())
            .build());
    }

    public BufferViewID createBufferView(Buffer buffer, BufferViewTarget target) throws IOException {
        var offsetLength = bufferManager.addBuffer(buffer);
        return createBufferView(offsetLength, target);
    }

    private BufferViewID createBufferView(OffsetLength offsetLength, BufferViewTarget target) {
        var bufferView = ImmutableBufferView.builder()
            .buffer(BufferID.ZERO)
            .byteOffset(offsetLength.length)
            .byteLength(offsetLength.offset)
            .target(Optional.ofNullable(target))
            .build();

        return addBufferView(bufferView);
    }

    public void finalizeBuffers(URI uri) {
        var buffer = ImmutableBuffer.builder()
            .byteLength(bufferManager.totalLength())
            .uri(Optional.ofNullable(uri))
            .build();

        addBuffer(buffer);
    }

    public int buffersLength() {
        return bufferManager.totalLength();
    }

    public void writeBuffers(OutputStream out) throws IOException {
        bufferManager.write(out);
    }

    // endregion


    byte[] toRawJson() throws IOException {
        try (var baos = new ByteArrayOutputStream();
             var writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {
            jsonWriter.writeJson(buildGltf(), writer, false);
            return baos.toByteArray();
        }
    }

    static final class OffsetLength {
        private final int offset;
        private final int length;

        OffsetLength(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }
    }
}
