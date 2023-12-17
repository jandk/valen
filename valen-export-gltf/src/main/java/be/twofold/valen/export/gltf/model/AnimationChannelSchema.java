package be.twofold.valen.export.gltf.model;

public record AnimationChannelSchema(
    int sampler,
    AnimationChannelTargetSchema target
) {
}
