package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.reader.resource.*;
import be.twofold.valen.game.eternal.resource.*;

import java.util.*;

public record EternalAsset(
    EternalAssetID key,
    int offset,
    int compressedSize,
    int uncompressedSize,
    ResourceCompressionMode compression,
    long hash,
    long checksum
) implements Asset {
    @Override
    public AssetID id() {
        return key;
    }

    @Override
    public AssetType type() {
        if (key.name().name().startsWith("generated/decls/material2/")) {
            return AssetType.MATERIAL;
        }
        return switch (key.type()) {
            case BaseModel, Model -> AssetType.MODEL;
            case Image -> AssetType.TEXTURE;
            default -> AssetType.BINARY;
        };
    }

    @Override
    public int size() {
        return uncompressedSize;
    }

    @Override
    public Map<String, Object> properties() {
        var properties = new HashMap<String, Object>();
        properties.put("hash", hash);
        properties.put("Type", key.type().toString());
        if (key.variation() != ResourceVariation.None) {
            properties.put("Variation", key.variation());
        }
        return properties;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EternalAsset other
            && key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
