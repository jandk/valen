package be.twofold.valen.gltf.model;

import org.immutables.value.*;

import java.util.*;

/**
 * Reference to a texture.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface NormalTextureInfoDef extends TextureInfoDef {
    /**
     * The scalar parameter applied to each normal vector of the normal texture.
     */
    OptionalDouble getScale();
}
