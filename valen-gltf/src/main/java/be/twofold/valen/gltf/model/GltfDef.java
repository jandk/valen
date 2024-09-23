package be.twofold.valen.gltf.model;

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
import org.immutables.value.*;

import java.util.*;

/**
 * The root object for a glTF asset.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface GltfDef extends GltfProperty {
    /**
     * Metadata about the glTF asset.
     */
    AssetSchema getAsset();

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
    Optional<SceneID> getScene();

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
