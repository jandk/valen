package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.greatcircle.reader.resources.*;
import be.twofold.valen.game.greatcircle.resource.*;

import java.util.*;

public record GreatCircleAsset(
    GreatCircleAssetID id,
    Location location,
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
            case image -> AssetType.TEXTURE;
            case basemodel, deformmodel, hair, model -> AssetType.MODEL;
            case material2 -> AssetType.MATERIAL;
            default -> AssetType.RAW;
        };
    }

    @Override
    public int size() {
        return switch (location) {
            case Location.Compressed compressed -> compressed.uncompressedSize();
            case Location.FileSlice fileSlice -> fileSlice.size();
            default -> 0;
        };
    }

    @Override
    public Map<String, Object> properties() {
        var properties = new HashMap<String, Object>();
        properties.put("hash", hash);
        properties.put("version", version);
        properties.put("Type", id.type().toString());
        if (id.variation() != ResourcesVariation.RES_VAR_NONE) {
            properties.put("Variation", id.variation());
        }
        return properties;
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
