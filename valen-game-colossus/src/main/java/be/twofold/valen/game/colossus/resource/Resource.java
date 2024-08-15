package be.twofold.valen.game.colossus.resource;

import be.twofold.valen.core.compression.*;

public record Resource(
    ResourceName name,
    ResourceType type,
    ResourceVariation variation,
    int offset,
    int compressedSize,
    int uncompressedSize,
    CompressionType compression,
    long hash
) {
    public ResourceKey key() {
        return new ResourceKey(name, type, variation);
    }
}
