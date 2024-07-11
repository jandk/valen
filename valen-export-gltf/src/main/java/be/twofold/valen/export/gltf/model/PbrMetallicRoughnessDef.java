package be.twofold.valen.export.gltf.model;

import be.twofold.valen.core.math.*;
import org.immutables.value.*;

import java.util.*;

/**
 * A set of parameter values that are used to define the metallic-roughness material model from Physically Based Rendering (PBR) methodology.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface PbrMetallicRoughnessDef extends GltfProperty {

    /**
     * The factors for the base color of the material.
     */
    Optional<Vector4> getBaseColorFactor();

    /**
     * The base color texture.
     */
    Optional<TextureInfoSchema> getBaseColorTexture();

    /**
     * The factor for the metalness of the material.
     */
    OptionalDouble getMetallicFactor();

    /**
     * The factor for the roughness of the material.
     */
    OptionalDouble getRoughnessFactor();

    /**
     * The metallic-roughness texture.
     */
    Optional<TextureInfoSchema> getMetallicRoughnessTexture();

}
