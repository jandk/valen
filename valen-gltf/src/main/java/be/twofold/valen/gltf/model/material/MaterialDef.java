package be.twofold.valen.gltf.model.material;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.texture.*;
import be.twofold.valen.gltf.types.*;
import org.immutables.value.*;

import java.util.*;

/**
 * The material appearance of a primitive.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface MaterialDef extends GltfChildOfRootProperty {
    /**
     * A set of parameter values that are used to define the metallic-roughness material model from Physically Based Rendering (PBR) methodology.
     * When undefined, all the default values of <pre>pbrMetallicRoughness</pre> <b>MUST</b> apply.
     */
    Optional<PbrMetallicRoughnessSchema> getPbrMetallicRoughness();

    /**
     * The tangent space normal texture.
     */
    Optional<NormalTextureInfoSchema> getNormalTexture();

    /**
     * The occlusion texture.
     */
    Optional<OcclusionTextureInfoSchema> getOcclusionTexture();

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
    OptionalDouble getAlphaCutoff();

    /**
     * Specifies whether the material is double sided.
     */
    Optional<Boolean> getDoubleSided();

}
