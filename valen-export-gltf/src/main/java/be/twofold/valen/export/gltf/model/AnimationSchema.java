package be.twofold.valen.export.gltf.model;

import java.util.*;

public record AnimationSchema(
    String name,
    List<AnimationChannelSchema> channels,
    List<AnimationSamplerSchema> samplers
) {
}
