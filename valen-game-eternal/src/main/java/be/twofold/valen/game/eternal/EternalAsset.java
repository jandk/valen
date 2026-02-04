package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.reader.resource.*;
import be.twofold.valen.game.eternal.resource.*;

import java.util.*;

public record EternalAsset(
    EternalAssetID id,
    StorageLocation location,
    long hash,
    long checksum
) implements Asset {
    @Override
    public AssetType type() {
        if (id.name().name().startsWith("generated/decls/material2/")) {
            return AssetType.MATERIAL;
        }
        return switch (id.type()) {
            case Anim -> AssetType.ANIMATION;
            case BaseModel, Model -> AssetType.MODEL;
            case Image -> AssetType.TEXTURE;
            default -> AssetType.RAW;
        };
    }

    @Override
    public int size() {
        return switch (location) {
            case StorageLocation.Compressed compressed -> compressed.uncompressedSize();
            case StorageLocation.FileSlice fileSlice -> fileSlice.size();
            default -> 0;
        };
    }

    @Override
    public Map<String, Object> properties() {
        var properties = new HashMap<String, Object>();
        properties.put("hash", hash);
        properties.put("Type", id.type().toString());
        if (id.variation() != ResourceVariation.RES_VAR_NONE) {
            properties.put("Variation", id.variation());
        }
        return properties;
    }

    @Override
    public String exportName() {
        if (id.type() == ResourceType.Image) {
            var hashString = HexFormat.of().toHexDigits(hash);
            return id.fileNameWithoutExtension() + "@" + hashString;
        }
        return id.fileNameWithoutExtension();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EternalAsset other
            && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
