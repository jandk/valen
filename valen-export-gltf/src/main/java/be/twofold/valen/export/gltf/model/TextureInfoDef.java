package be.twofold.valen.export.gltf.model;

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
    TextureId getIndex();

    /**
     * The set index of texture’s TEXCOORD attribute used for texture coordinate mapping.
     */
    Optional<Integer> getTexCoord();
}
