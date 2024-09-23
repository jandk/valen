package be.twofold.valen.gltf.model.material;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.texture.*;
import org.immutables.value.*;

import java.util.*;

/**
 * Reference to a texture.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface NormalTextureInfoDef extends TextureInfo {
    /**
     * The scalar parameter applied to each normal vector of the normal texture.
     */
    OptionalDouble getScale();
}
