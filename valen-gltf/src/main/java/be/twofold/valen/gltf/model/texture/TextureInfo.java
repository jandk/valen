package be.twofold.valen.gltf.model.texture;

import be.twofold.valen.gltf.model.*;

import java.util.*;

public interface TextureInfo extends GltfProperty {
    /**
     * The index of the texture.
     */
    TextureID getIndex();

    /**
     * The set index of texture’s TEXCOORD attribute used for texture coordinate mapping.
     */
    OptionalInt getTexCoord();
}
