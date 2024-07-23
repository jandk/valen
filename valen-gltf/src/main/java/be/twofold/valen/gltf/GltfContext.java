package be.twofold.valen.gltf;

import be.twofold.valen.gltf.model.*;

import java.nio.*;
import java.security.cert.Extension;
import java.util.*;

public class GltfContext {
    private final Map<String, Extension> extensions = new TreeMap<>();
    private final List<String> extensionsUsed = new ArrayList<>();
    private final List<String> extensionsRequired = new ArrayList<>();
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

    private final List<String> allocatedTextures = new ArrayList<>();

    final List<Buffer> writables = new ArrayList<>();

    List<BufferSchema> getBuffers() {
        return buffers;
    }

    List<BufferViewSchema> getBufferViews() {
        return bufferViews;
    }

    public void addExtension(String name, Extension extension, boolean required) {
        extensionsUsed.add(name);
        if (required) {
            extensionsRequired.add(name);
        }
        extensions.put(name, extension);
    }

    public SceneSchema addScene(List<NodeId> nodes) {
        var skinNodes = skins.stream()
            .flatMap(skin -> skin.getSkeleton().stream())
            .toList();

        var scene = SceneSchema.builder()
            .addAllNodes(nodes)
            .addAllNodes(skinNodes)
            .build();
        scenes.add(scene);
        return scene;
    }

    public AccessorId addAccessor(AccessorSchema accessor) {
        accessors.add(accessor);
        return AccessorId.of(accessors.size() - 1);
    }

    public NodeId addNode(NodeSchema node) {
        nodes.add(node);
        return NodeId.of(nodes.size() - 1);
    }

    public BufferViewId createBufferView(Buffer buffer, int length, BufferViewTarget target) {
        writables.add(buffer);
        var bufferId = writables.size() - 1;
        return createBufferView(length, target, bufferId);
    }

    public TextureId allocateTextureId(String textureName) {
        var textureId = allocatedTextures.indexOf(textureName);
        if (textureId == -1) {
            allocatedTextures.add(textureName);
            return TextureId.of(allocatedTextures.size() - 1);
        } else {
            return TextureId.of(textureId);
        }
    }

    public List<String> getAllocatedTextures() {
        return allocatedTextures;
    }

    public MaterialId addMaterial(MaterialSchema material) {
        materials.add(material);
        return MaterialId.of(materials.size() - 1);
    }

    public MaterialId findMaterial(String materialName) {
        for (int i = 0; i < materials.size(); i++) {
            MaterialSchema materialSchema = materials.get(i);
            if (materialSchema.getName().isPresent() && materialSchema.getName().get().equals(materialName)) {
                return MaterialId.of(i);
            }
        }
        return MaterialId.of(-1);
    }

    public ImageId getImage(String texturePath) {
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).getName().isPresent() && images.get(i).getName().get().equals(texturePath)) {
                return ImageId.of(i);
            }
        }
        return ImageId.of(-1);
    }

    public ImageId addImage(ImageSchema image) {
        images.add(image);
        return ImageId.of(images.size() - 1);
    }

    public TextureId addTexture(TextureSchema texture) {
        textures.add(texture);
        return TextureId.of(textures.size() - 1);
    }

    private BufferViewId createBufferView(int length, BufferViewTarget target, int bufferId) {
        var bufferView = BufferViewSchema.builder()
            .buffer(BufferId.of(bufferId))
            .byteOffset(0)
            .byteLength(length)
            .target(Optional.ofNullable(target))
            .build();
        bufferViews.add(bufferView);

        // Round up offset to a multiple of 4
        return BufferViewId.of(bufferViews.size() - 1);
    }

    public GltfSchema buildGltf() {
        var asset = AssetSchema.builder()
            .generator("Valen")
            .version("2.0")
            .build();

        return GltfSchema.builder()
            // .extensions(extensions)
            .extensionsUsed(extensionsUsed)
            .extensionsRequired(extensionsRequired)
            .accessors(accessors)
            .animations(animations)
            .asset(asset)
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

    public MeshId addMesh(MeshSchema mesh) {
        meshes.add(mesh);
        return MeshId.of(meshes.size() - 1);
    }

    public SkinId addSkin(SkinSchema skin) {
        skins.add(skin);
        return SkinId.of(skins.size() - 1);
    }

    public NodeId nextNodeId() {
        return NodeId.of(nodes.size());
    }
}
