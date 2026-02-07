package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.game.io.*;
import be.twofold.valen.game.darkages.reader.resources.*;

import java.util.*;

public record DarkAgesAsset(
    DarkAgesAssetID id,
    Location location,
    long hash,
    long checksum,
    int version
) implements Asset {
    @Override
    public AssetType type() {
        if (id.name().name().startsWith("generated/decls/material2/")) {
            return AssetType.MATERIAL;
        }
        return switch (id.type()) {
            case Anim -> AssetType.ANIMATION;
            case BaseModel, Model, StrandsHair, Vegetation -> AssetType.MODEL;
            case Image -> AssetType.TEXTURE;
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
        properties.put("Type", id.type().toString());
        if (id.variation() != ResourcesVariation.RES_VAR_NONE) {
            properties.put("Variation", id.variation());
        }
        return properties;
    }

    @Override
    public String exportName() {
        if (id.type() == ResourcesType.Image) {
            var hashString = HexFormat.of().toHexDigits(hash);
            return id.fileNameWithoutExtension() + "@" + hashString;
        }
        return id.fileNameWithoutExtension();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DarkAgesAsset other
            && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
