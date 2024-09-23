package be.twofold.valen.gltf.model.texture;

import be.twofold.valen.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

/**
 * Reference to a texture.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface TextureInfoDef extends GltfProperty {
    /**
     * The index of the texture.
     */
    TextureID getIndex();

    /**
     * The set index of texture’s TEXCOORD attribute used for texture coordinate mapping.
     */
    OptionalInt getTexCoord();
}
