package be.twofold.valen.writer.gltf.model;

public record AnimationChannelSchema(
    int sampler,
    AnimationChannelTargetSchema target
) {
}
