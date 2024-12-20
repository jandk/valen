package org.redeye.valen.game.halflife;

import be.twofold.valen.core.game.*;

public record HalfLifeAssetID(String archive, String name) implements AssetID {
    @Override
    public String fullName() {
        return name;
    }

    public String archive() {
        return archive;
    }

    public AssetType<?> identifyAssetType() {
        if (fullName().endsWith(".mdl")) {
            return AssetType.MODEL;
        }
        return AssetType.BINARY;
    }
}
