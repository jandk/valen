package be.twofold.valen.format.gltf.model.material;

import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.texture.*;
import be.twofold.valen.format.gltf.types.*;
import org.immutables.value.*;

import java.util.*;

/**
 * The material appearance of a primitive.
 */
@Schema2Style
@Value.Immutable
public interface MaterialSchema extends GltfChildOfRootProperty {

    /**
     * A set of parameter values that are used to define the metallic-roughness material model from Physically Based
     * Rendering (PBR) methodology. When undefined, all the default values of {@code pbrMetallicRoughness} <b>MUST</b>
     * apply.
     */
    Optional<MaterialPbrMetallicRoughnessSchema> getPbrMetallicRoughness();

    /**
     * The tangent space normal texture.
     */
    Optional<MaterialNormalTextureInfoSchema> getNormalTexture();

    /**
     * The occlusion texture.
     */
    Optional<MaterialOcclusionTextureInfoSchema> getOcclusionTexture();

    /**
     * The emissive texture.
     */
    Optional<TextureInfoSchema> getEmissiveTexture();

    /**
     * The factors for the emissive color of the material.
     */
    Optional<Vec3> getEmissiveFactor();

    /**
     * The alpha rendering mode of the material.
     */
    Optional<MaterialAlphaMode> getAlphaMode();

    /**
     * The alpha cutoff value of the material.
     */
    Optional<Float> getAlphaCutoff();

    /**
     * Specifies whether the material is double sided.
     */
    Optional<Boolean> isDoubleSided();

}
