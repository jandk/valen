package be.twofold.valen.export.gltf.model;

import org.immutables.value.*;

import java.util.*;

/**
 * The root object for a glTF asset.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface GltfDef extends GltfProperty {
    /**
     * Names of glTF extensions used in this asset.
     */
    List<String> getExtensionsUsed();

    /**
     * Names of glTF extensions required to properly load this asset.
     */
    List<String> getExtensionsRequired();

    /**
     * An array of accessors.
     */
    List<AccessorSchema> getAccessors();

    /**
     * An array of keyframe animations.
     */
    List<AnimationSchema> getAnimations();

    /**
     * Metadata about the glTF asset.
     */
    AssetSchema getAsset();

    /**
     * An array of buffers.
     */
    List<BufferSchema> getBuffers();

    /**
     * An array of bufferViews.
     */
    List<BufferViewSchema> getBufferViews();

    /**
     * An array of cameras.
     */
    List<CameraSchema> getCameras();

    /**
     * An array of images.
     */
    List<ImageSchema> getImages();

    /**
     * An array of materials.
     */
    List<MaterialSchema> getMaterials();

    /**
     * An array of meshes.
     */
    List<MeshSchema> getMeshes();

    /**
     * An array of nodes.
     */
    List<NodeSchema> getNodes();

    /**
     * An array of samplers.
     */
    List<SamplerSchema> getSamplers();

    /**
     * The index of the default scene.
     */
    OptionalInt getScene();

    /**
     * An array of scenes.
     */
    List<SceneSchema> getScenes();

    /**
     * An array of skins.
     */
    List<SkinSchema> getSkins();

    /**
     * An array of textures.
     */
    List<TextureSchema> getTextures();

}
