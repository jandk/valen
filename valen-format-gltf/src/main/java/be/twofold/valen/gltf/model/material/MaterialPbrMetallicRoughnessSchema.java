package be.twofold.valen.gltf.model.material;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.texture.*;
import be.twofold.valen.gltf.types.*;
import org.immutables.value.*;

import java.util.*;

/**
 * A set of parameter values that are used to define the metallic-roughness material model from Physically-Based
 * Rendering (PBR) methodology.
 */
@Schema2Style
@Value.Immutable
public interface MaterialPbrMetallicRoughnessSchema extends GltfProperty {

    /**
     * The factors for the base color of the material.
     */
    Optional<Vec4> getBaseColorFactor();

    /**
     * The base color texture.
     */
    Optional<TextureInfoSchema> getBaseColorTexture();

    /**
     * The factor for the metalness of the material.
     */
    Optional<Float> getMetallicFactor();

    /**
     * The factor for the roughness of the material.
     */
    Optional<Float> getRoughnessFactor();

    /**
     * The metallic-roughness texture.
     */
    Optional<TextureInfoSchema> getMetallicRoughnessTexture();

}
