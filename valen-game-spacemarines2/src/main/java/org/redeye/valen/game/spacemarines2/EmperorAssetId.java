package org.redeye.valen.game.spacemarines2;

import be.twofold.valen.core.game.*;

public record EmperorAssetId(String path) implements AssetID {
    @Override
    public String fullName() {
        return path;
    }

    public AssetType inferAssetType() {
        var index = fileName().indexOf('.');
        if (index == -1) {
            return AssetType.RAW;
        }
        String ext = fileName().substring(index + 1);

        return switch (ext) {
            case "pct.resource" -> AssetType.TEXTURE;
            case "tpl" -> AssetType.MODEL;
            case "td" -> AssetType.DATA;
            default -> {
                if (ext.endsWith(".resource")) {
                    yield AssetType.DATA;
                }
                yield AssetType.RAW;
            }
        };
    }

    public EmperorAssetId withExtension(String newExtension) {
        var index = path.lastIndexOf('.');
        var withoutExt = path.substring(0, index);
        return new EmperorAssetId(withoutExt + newExtension);
    }
}
