package be.twofold.valen.gltf.model.material;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.texture.*;
import org.immutables.value.*;

import java.util.*;

/**
 * Material Occlusion Texture Info
 */
@Schema2Style
@Value.Immutable
public interface MaterialOcclusionTextureInfoSchema extends TextureInfoSchema {

    /**
     * A scalar multiplier controlling the amount of occlusion applied.
     */
    Optional<Float> getStrength();

}
