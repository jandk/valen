package be.twofold.valen.gltf.model.texture;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.image.*;
import be.twofold.valen.gltf.model.sampler.*;
import org.immutables.value.*;

import java.util.*;

/**
 * A texture and its sampler.
 */
@Schema2Style
@Value.Immutable
public interface TextureSchema extends GltfChildOfRootProperty {

    /**
     * The index of the sampler used by this texture. When undefined, a sampler with repeat wrapping and auto filtering
     * <b>SHOULD</b> be used.
     */
    Optional<SamplerID> getSampler();

    /**
     * The index of the image used by this texture. When undefined, an extension or other mechanism <b>SHOULD</b> supply
     * an alternate texture source, otherwise behavior is undefined.
     */
    Optional<ImageID> getSource();

}
