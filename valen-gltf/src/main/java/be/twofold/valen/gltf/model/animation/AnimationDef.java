package be.twofold.valen.gltf.model.animation;

import be.twofold.valen.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

@SchemaStyle
@Value.Immutable(copy = false)
public interface AnimationDef extends GltfChildOfRootProperty {
    /**
     * An array of animation channels.
     */
    List<AnimationChannelSchema> getChannels();

    /**
     * An array of animation samplers.
     */
    List<AnimationSamplerSchema> getSamplers();
}
