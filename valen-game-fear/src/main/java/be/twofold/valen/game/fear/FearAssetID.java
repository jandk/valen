package be.twofold.valen.game.fear;

import be.twofold.valen.core.game.*;

public record FearAssetID(String name) implements AssetID {
    @Override
    public String fullName() {
        return name.replace('\\', '/');
    }
}
