package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;

public record DoomAssetID(String name) implements AssetID {
    @Override
    public String fullName() {
        return name;
    }
}
