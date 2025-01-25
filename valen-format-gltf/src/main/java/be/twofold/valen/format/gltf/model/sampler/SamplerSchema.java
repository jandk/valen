package be.twofold.valen.format.gltf.model.sampler;

import be.twofold.valen.format.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

/**
 * Texture sampler properties for filtering and wrapping modes.
 */
@Schema2Style
@Value.Immutable
public interface SamplerSchema extends GltfChildOfRootProperty {

    /**
     * Magnification filter.
     */
    Optional<SamplerFilterType> getMagFilter();

    /**
     * Minification filter.
     */
    Optional<SamplerFilterType> getMinFilter();

    /**
     * S (U) wrapping mode.
     */
    Optional<SamplerWrappingType> getWrapS();

    /**
     * T (V) wrapping mode.
     */
    Optional<SamplerWrappingType> getWrapT();

}
