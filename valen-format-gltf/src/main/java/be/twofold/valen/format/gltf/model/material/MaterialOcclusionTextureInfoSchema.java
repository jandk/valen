package be.twofold.valen.format.gltf.model.material;

import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.texture.*;
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
