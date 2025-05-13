package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.darkages.reader.resources.*;

import java.util.*;

public record DarkAgesAsset(
        DarkAgesAssetID id,
        int offset,
        int compressedSize,
        int size,
        ResourcesCompressionMode compression,
        long hash,
        long checksum
) implements Asset {
    @Override
    public AssetType type() {
//        if (id.name().name().startsWith("generated/decls/material2/")) {
//            return AssetType.MATERIAL;
//        }
        return switch (id.type()) {
//            case Anim -> AssetType.ANIMATION;
//            case BaseModel, Model -> AssetType.MODEL;
            case Image -> AssetType.TEXTURE;
            default -> AssetType.RAW;
        };
    }

    @Override
    public Map<String, Object> properties() {
        var properties = new HashMap<String, Object>();
        properties.put("hash", hash);
        properties.put("Type", id.type().toString());
//        if (id.variation() != ResourcesVariation.None) {
//            properties.put("Variation", id.variation());
//        }
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
