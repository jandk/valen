package be.twofold.valen.resource;

public record ResourceKey(
    ResourceName name,
    ResourceType type,
    ResourceVariation variation
) {
}
