package be.twofold.valen.gltf.model.texture;

import be.twofold.valen.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

/**
 * Reference to a texture.
 */
@Schema2Style
@Value.Immutable
public interface TextureInfoSchema extends GltfProperty {

    /**
     * The index of the texture. (Required)
     */
    TextureID getIndex();

    /**
     * The set index of texture's TEXCOORD attribute used for texture coordinate mapping.
     */
    Optional<Integer> getTexCoord();

}
