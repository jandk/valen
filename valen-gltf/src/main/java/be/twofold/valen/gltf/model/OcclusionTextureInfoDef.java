package be.twofold.valen.gltf.model;

import org.immutables.value.*;

import java.util.*;

/**
 * Reference to a texture.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface OcclusionTextureInfoDef extends TextureInfoDef {
    /**
     * A scalar multiplier controlling the amount of occlusion applied.
     */
    OptionalDouble getStrength();
}
