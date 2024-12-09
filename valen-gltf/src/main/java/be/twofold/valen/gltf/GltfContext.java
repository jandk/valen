package be.twofold.valen.gltf;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.accessor.*;
import be.twofold.valen.gltf.model.animation.*;
import be.twofold.valen.gltf.model.asset.*;
import be.twofold.valen.gltf.model.buffer.*;
import be.twofold.valen.gltf.model.camera.*;
import be.twofold.valen.gltf.model.image.*;
import be.twofold.valen.gltf.model.material.*;
import be.twofold.valen.gltf.model.mesh.*;
import be.twofold.valen.gltf.model.node.*;
import be.twofold.valen.gltf.model.sampler.*;
import be.twofold.valen.gltf.model.scene.*;
import be.twofold.valen.gltf.model.skin.*;
import be.twofold.valen.gltf.model.texture.*;

import java.net.*;
import java.nio.*;
import java.util.*;

public final class GltfContext {
    private final Map<String, Extension> extensions = new TreeMap<>();
    private final Set<String> extensionsUsed = new TreeSet<>();
    private final Set<String> extensionsRequired = new TreeSet<>();
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

    private final List<Buffer> binaryBuffers = new ArrayList<>();
    private int binaryBufferSize = 0;

    // region Getters

    public List<AccessorSchema> getAccessors() {
        return Collections.unmodifiableList(accessors);
    }

    public List<AnimationSchema> getAnimations() {
        return Collections.unmodifiableList(animations);
    }

    public List<BufferSchema> getBuffers() {
        return Collections.unmodifiableList(buffers);
    }

    public List<BufferViewSchema> getBufferViews() {
        return Collections.unmodifiableList(bufferViews);
    }

    public List<CameraSchema> getCameras() {
        return Collections.unmodifiableList(cameras);
    }

    public List<ImageSchema> getImages() {
        return Collections.unmodifiableList(images);
    }

    public List<MaterialSchema> getMaterials() {
        return Collections.unmodifiableList(materials);
    }

    public List<MeshSchema> getMeshes() {
        return Collections.unmodifiableList(meshes);
    }

    public List<NodeSchema> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public List<SamplerSchema> getSamplers() {
        return Collections.unmodifiableList(samplers);
    }

    public List<SceneSchema> getScenes() {
        return Collections.unmodifiableList(scenes);
    }

    public List<SkinSchema> getSkins() {
        return Collections.unmodifiableList(skins);
    }

    public List<TextureSchema> getTextures() {
        return Collections.unmodifiableList(textures);
    }

    public List<Buffer> getBinaryBuffers() {
        return Collections.unmodifiableList(binaryBuffers);
    }

    // endregion

    // region Adders

    public void registerExtension(Extension extension) {
        extensionsUsed.add(extension.getName());
        if (extension.isRequired()) {
            extensionsRequired.add(extension.getName());
        }
    }

    public void registerExtensions(GltfProperty property) {
        property.getExtensions().values()
            .forEach(this::registerExtension);
    }

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
        registerExtensions(animation);
        animations.add(animation);
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
        return SkinID.of(skins.size() - 1);
    }

    public TextureID addTexture(TextureSchema texture) {
        textures.add(texture);
        registerExtensions(texture);
        return TextureID.of(textures.size() - 1);
    }

    // endregion

    public void addScene(List<NodeID> nodes) {
        var skinNodes = skins.stream()
            .flatMap(skin -> skin.getSkeleton().stream())
            .toList();

        scenes.add(SceneSchema.builder()
            .addAllNodes(nodes)
            .addAllNodes(skinNodes)
            .build());
    }

    public BufferViewID createBufferView(Buffer buffer) {
        return createBufferView(buffer, null);
    }

    public BufferViewID createBufferView(Buffer buffer, BufferViewTarget target) {
        var bufferView = BufferViewSchema.builder()
            .buffer(BufferID.of(0))
            .byteLength(size(buffer))
            .byteOffset(binaryBufferSize)
            .target(Optional.ofNullable(target))
            .build();

        binaryBuffers.add(buffer);
        binaryBufferSize = GltfUtils.alignedLength(binaryBufferSize + size(buffer));

        return addBufferView(bufferView);
    }

    public GltfSchema buildGltf() {
        var asset = AssetSchema.builder()
            .version("2.0")
            .generator("Valen")
            .build();

        return GltfSchema.builder()
            .asset(asset)
            // .extensions(extensions)
            .extensionsUsed(extensionsUsed)
            .extensionsRequired(extensionsRequired)
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

    public int updateBufferViews(URI uri) {
        buffers.clear();
        addBuffer(BufferSchema.builder()
            .byteLength(binaryBufferSize)
            .uri(Optional.ofNullable(uri))
            .build());
        return binaryBufferSize;
    }

    private int size(Buffer buffer) {
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
}
