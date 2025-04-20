package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.greatcircle.reader.resources.*;
import be.twofold.valen.game.greatcircle.resource.*;

import java.util.*;

public record GreatCircleAsset(
    GreatCircleAssetID id,
    long offset,
    int compressedSize,
    int uncompressedSize,
    ResourceCompressionMode compression,
    long hash,
    long checksum,
    int version
) implements Asset {
    @Override
    public GreatCircleAssetID id() {
        return id;
    }

    @Override
    public AssetType type() {
        return switch (id.type()) {
            case ResourceType.image -> AssetType.TEXTURE;
            case ResourceType.model -> AssetType.MODEL;
            default -> AssetType.RAW;
        };
    }

    @Override
    public int size() {
        return uncompressedSize;
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of(
            "hash", hash,
            "version", version
        );
    }

    @Override
    public String exportName() {
        if (id.type() == ResourceType.image) {
            var hashString = HexFormat.of().toHexDigits(hash);
            return id.fileNameWithoutExtension() + "@" + hashString;
        }
        return id.fileNameWithoutExtension();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GreatCircleAsset other
            && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
