package org.redeye.valen.game.source1;

import be.twofold.valen.core.game.*;

public record SourceAssetID(String source, String name) implements AssetID {
    @Override
    public String fullName() {
        return name;
    }

    @Override
    public String pathName() {
        var index = name.lastIndexOf('/');
        return index == -1 ? "" : name.substring(0, index);
    }

    @Override
    public String fileName() {
        var index = name.lastIndexOf('/');
        return index == -1 ? name : name.substring(index + 1);
    }

    public String extension() {
        var index = name.lastIndexOf('.');
        return index == -1 ? name : name.substring(index + 1);
    }


    public AssetType<?> identifyAssetType() {
        return switch (extension()) {
            case "vtf" -> AssetType.TEXTURE;
            case "vmt", "cfg", "txt", "res", "vdf" -> AssetType.TEXT;
            case "mdl" -> AssetType.MODEL;
            default -> AssetType.BINARY;
        };
    }

}
