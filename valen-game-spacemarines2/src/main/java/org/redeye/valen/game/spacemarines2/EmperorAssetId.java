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


    public AssetType inferAssetType() {
        var index = fileName().indexOf('.');
        if (index == -1) {
            return AssetType.Binary;
        }
        String ext = fileName().substring(index + 1);

        return switch (ext) {
            case "pct.resource" -> AssetType.Texture;
            case "tpl" -> AssetType.Model;
            case "td" -> AssetType.Data;
            default -> {
                if (ext.endsWith(".resource")) {
                    yield AssetType.Data;
                }
                yield AssetType.Binary;
            }
        };
    }

    public AssetID withExt(String newExt) {
        var index = path.lastIndexOf('.');
        var withoutExt = path.substring(0, index);
        return new EmperorAssetId(withoutExt + newExt);
    }
}
