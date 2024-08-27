package org.redeye.valen.game.halflife2;

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

    public String extention() {
        var index = name.lastIndexOf('.');
        return index == -1 ? name : name.substring(index + 1);
    }


    public AssetType identifyAssetType() {
        switch (extention()) {
            case "vtf":
                return AssetType.Image;
            default:
                return AssetType.Binary;
        }
    }

}
