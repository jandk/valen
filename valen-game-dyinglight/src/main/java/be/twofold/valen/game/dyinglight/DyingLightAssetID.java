package be.twofold.valen.game.dyinglight;

import be.twofold.valen.core.game.*;

public record DyingLightAssetID(String name) implements AssetID {
    @Override
    public String fullName() {
        return name;
    }
}
