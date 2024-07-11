package be.twofold.valen.export.gltf;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.export.gltf.mappers.*;
import be.twofold.valen.export.gltf.model.*;

import java.nio.*;
import java.util.*;

public class GltfContext {
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
    private final List<MaterialSchema> materials = new ArrayList<>();
    private final List<TextureSchema> textures = new ArrayList<>();
    private final List<ImageSchema> images = new ArrayList<>();

    private final List<String> allocatedTextures = new ArrayList<>();

    private final GltfModelMapper modelMapper = new GltfModelMapper(this);
    private final GltfSkeletonMapper skeletonMapper = new GltfSkeletonMapper(this);
    private final GltfAnimationMapper animationMapper = new GltfAnimationMapper(this);

    final List<Buffer> writables = new ArrayList<>();

    List<BufferSchema> getBuffers() {
        return buffers;
    }

    List<BufferViewSchema> getBufferViews() {
        return bufferViews;
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

    public MeshId addMesh(Model model) {
        meshes.add(modelMapper.map(model));
        return MeshId.of(meshes.size() - 1);
    }

    public AbstractMap.SimpleEntry<NodeId, SkinId> addSkin(Skeleton skeleton) {
        var skeletonRootNodeId = NodeId.of(nodes.size());
        var skin = skeletonMapper.map(skeleton, nodes.size());
        skins.add(skin);
        return new AbstractMap.SimpleEntry<>(skeletonRootNodeId, SkinId.of(skins.size() - 1));
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
            .materials(materials)
            .textures(textures)
            .images(images)
            .build();
    }
}
