package be.twofold.valen.format.gltf.model.material;

import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.texture.*;
import org.immutables.value.*;

import java.util.*;

/**
 * Material Normal Texture Info
 */
@SchemaStyle
@Value.Immutable
public interface MaterialNormalTextureInfoSchema extends TextureInfoSchema {

    /**
     * The scalar parameter applied to each normal vector of the normal texture.
     */
    Optional<Float> getScale();

}
