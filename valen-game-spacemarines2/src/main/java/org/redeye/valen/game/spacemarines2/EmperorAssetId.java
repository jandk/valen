package org.redeye.valen.game.spacemarines2;

import be.twofold.valen.core.game.*;

public record EmperorAssetId(String path) implements AssetID {
    @Override
    public String fullName() {
        return path;
    }

    @Override
    public String pathName() {
        var index = path.lastIndexOf('/');
        return index == -1 ? "" : path.substring(0, index);
    }

    @Override
    public String fileName() {
        var index = path.lastIndexOf('/');
        return index == -1 ? path : path.substring(index + 1);
    }

    public AssetType<?> inferAssetType() {
        var index = fileName().indexOf('.');
        if (index == -1) {
            return AssetType.BINARY;
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
                yield AssetType.BINARY;
            }
        };
    }

    public EmperorAssetId withExt(String newExt) {
        var index = path.lastIndexOf('.');
        var withoutExt = path.substring(0, index);
        return new EmperorAssetId(withoutExt + newExt);
    }
}
